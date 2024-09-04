package com.example.electricitybill.controller;

import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.service.ElectricityRateService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/electricity-rate")
public class ElectricityRateController {

    @Autowired
    private ElectricityRateService electricityRateService;

    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n"; // Replace with your actual signing key

    @PostMapping("/add")
    public ResponseEntity<?> addElectricityRate(@RequestHeader("Authorization") String token, @RequestBody ElectricityRate electricityRate) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        ElectricityRate newRate = electricityRateService.addElectricityRate(electricityRate);
        return new ResponseEntity<>(newRate, HttpStatus.CREATED);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateElectricityRate(@RequestHeader("Authorization") String token, @RequestBody ElectricityRate electricityRate) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        ElectricityRate updatedRate = electricityRateService.updateElectricityRateUsingQueryDSL(electricityRate);
        return new ResponseEntity<>(updatedRate, HttpStatus.OK);
    }
}