package com.example.electricitybill.service;

import com.example.electricitybill.model.User;

import com.example.electricitybill.repository.User.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n";

    public ResponseEntity<?> addUser(String token, User user) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        User newUser = userRepository.save(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateUser(String token, Long id, User user) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        User updatedUser = userRepository.findByPhoneNumber(user.getPhoneNumber())
                .map(existingUser -> {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPhoneNumber(user.getPhoneNumber());
                    existingUser.setType(user.getType());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(String token, int id) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        userRepository.deleteUserUsingQueryDSL(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<?> getUserById( int  phoneNumber) {
        User user = userRepository.findUserByPhoneNumberUsingQueryDSL(phoneNumber);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<?> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("role", userOptional.get().getRole())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                    .signWith(SignatureAlgorithm.HS256, signingKey)
                    .compact();
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
}