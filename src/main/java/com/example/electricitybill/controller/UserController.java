package com.example.electricitybill.controller;

import com.example.electricitybill.model.BlacklistedToken;
import com.example.electricitybill.model.User;
import com.example.electricitybill.repository.BillInfo.BillInfoRepository;
import com.example.electricitybill.repository.BlacklistRepository;
import com.example.electricitybill.repository.User.UserRepository;
import com.example.electricitybill.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final BillInfoRepository billInfoRepository;
    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n";

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private UserService userService;

    private String generateToken(String phoneNumber, String email, String password, String role, String type) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(signingKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + 3600000;
        Date exp = new Date(expMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(phoneNumber)
                .claim("email", email)
                .claim("password", password)
                .claim("role", role)
                .claim("type", type) // Add the type claim
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    private String refreshTokenIfExpiredAndActive(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();

        Optional<BlacklistedToken> blacklistedToken = blacklistRepository.findById(token);

        if (now.after(expiration) && blacklistedToken.isPresent()) {
            String phoneNumber = claims.getSubject();
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String role = (String) claims.get("role");
            String type = (String) claims.get("type");

            return generateToken(phoneNumber, email, password, role, type);
        }

        return token;
    }

    //add
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User newUser) {
        Optional<User> existingUserWithSamePhoneNumber = userRepository.findByPhoneNumber(newUser.getPhoneNumber());

        if(newUser.getPhoneNumber().length() != 10){
            return new ResponseEntity<>("Invalid phone number. It must be 10 digits long.", HttpStatus.BAD_REQUEST);
        }
        if (!newUser.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if(newUser.getPassword().length() < 8){
            return new ResponseEntity<>("Invalid password. It must be at least 8 characters long.", HttpStatus.BAD_REQUEST);
        }

        if (existingUserWithSamePhoneNumber.isPresent()) {
            return new ResponseEntity<>("A user with the same phone number already exists", HttpStatus.CONFLICT);
        }
        if(newUser.getPhoneNumber() == null || newUser.getPhoneNumber().isEmpty()){
            return new ResponseEntity<>("Phone number is required", HttpStatus.BAD_REQUEST);
        }
        if(newUser.getEmail() == null || newUser.getEmail().isEmpty()){
            return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
        }
        if(newUser.getPassword() == null || newUser.getPassword().isEmpty()){
            return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
        }
        if(newUser.getType() == null || newUser.getType().isEmpty()){
            return new ResponseEntity<>("Type is required", HttpStatus.BAD_REQUEST);
        }




        Optional<User> existingUserWithSameEmail = userRepository.findByEmail(newUser.getEmail());
        if (existingUserWithSameEmail.isPresent()) {
            return new ResponseEntity<>("A user with the same email already exists", HttpStatus.CONFLICT);
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole("USER");
        newUser.setType(newUser.getType());
        userRepository.save(newUser);
        String token = generateToken(newUser.getPhoneNumber(), newUser.getEmail(), newUser.getPassword(), newUser.getRole() , newUser.getType());
        return new ResponseEntity<>("User added successfully. Token: " + token, HttpStatus.CREATED);
    }

    // update
    @PutMapping("/update/{phoneNumber}")
    public ResponseEntity<String> updateUser(@PathVariable String phoneNumber, @RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String tokenPhoneNumber = claims.getSubject();
        String role = (String) claims.get("role");

        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        if (!existingUser.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!phoneNumber.equals(tokenPhoneNumber) && !"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        if (updatedUser.getEmail() == null || !updatedUser.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if (updatedUser.getPassword() == null || updatedUser.getPassword().length() < 8) {
            return new ResponseEntity<>("Invalid password. It must be at least 8 characters long.", HttpStatus.BAD_REQUEST);
        }

        User userToUpdate = userService.updateUserUsingQueryDSL(phoneNumber, updatedUser);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    // delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        boolean isDeleted = userService.deleteUserUsingQueryDSL(id);
        if (!isDeleted) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/findbyphonenum")
    public ResponseEntity<?> findUser(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String token) {
        String phoneNumber = body.get("phoneNumber");

        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String tokenPhoneNumber = claims.getSubject();
        String role = (String) claims.get("role");

        if (!phoneNumber.equals(tokenPhoneNumber) && !"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        User existingUser = userService.findUserByPhoneNumberUsingQueryDSL(phoneNumber);
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(existingUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginUser) {
        Optional<User> existingUser = userRepository.findByPhoneNumber(loginUser.getPhoneNumber());
        if (!existingUser.isPresent()) {
            return new ResponseEntity<>(Map.of("message", "Login failure: Account or password is incorrect"), HttpStatus.NOT_FOUND);
        }

        User user = existingUser.get();
        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(Map.of("message", "Login failure: Account or password is incorrect"), HttpStatus.UNAUTHORIZED);
        }
        String token = generateToken(user.getPhoneNumber(), user.getEmail(), user.getPassword(), user.getRole(),user.getType());
        Date loginTime = new Date();
        blacklistRepository.save(new BlacklistedToken(token, loginTime));
        return new ResponseEntity<>(Map.of("message", "Logged in successfully.", "token", token, "role", user.getRole()), HttpStatus.OK);
    }

    //get all users by admin
    @GetMapping("/allusers")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("not admin ", HttpStatus.FORBIDDEN);
        }

        List<User> users = userService.getAllUsersUsingQueryDSL();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/grantAdmin")
    public ResponseEntity<String> grantAdmin(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String adminToken) {
        String phoneNumber = body.get("phoneNumber");

        Claims adminClaims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(adminToken.replace("Bearer ", ""))
                .getBody();

        String adminRole = (String) adminClaims.get("role");

        if (!"admin".equals(adminRole)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        user.setRole("admin");
        userRepository.save(user);

        return new ResponseEntity<>("User granted admin successfully", HttpStatus.OK);
    }

    @PostMapping("/revokeAdmin")
    public ResponseEntity<String> revokeAdmin(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String adminToken) {
        String phoneNumber = body.get("phoneNumber");

        Claims adminClaims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(adminToken.replace("Bearer ", ""))
                .getBody();

        String adminRole = (String) adminClaims.get("role");

        if (!"admin".equals(adminRole)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if (!"admin".equals(user.getRole())) {
            return new ResponseEntity<>("User is not an admin", HttpStatus.BAD_REQUEST);
        }

        user.setRole("USER");
        userRepository.save(user);

        return new ResponseEntity<>("User admin rights revoked successfully", HttpStatus.OK);
    }
}





