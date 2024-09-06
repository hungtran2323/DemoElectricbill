package com.example.electricitybill.controller;

import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.service.ElectricityRateService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        ElectricityRate newRate = electricityRateService.saveElectricityRate(electricityRate);
        return new ResponseEntity<>(newRate, HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateElectricityRate(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody ElectricityRate electricityRate) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        ElectricityRate updatedRate = electricityRateService.updateElectricityRateUsingQueryDSL(id, electricityRate);
        return new ResponseEntity<>(updatedRate, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{tier}")
    public ResponseEntity<?> deleteElectricityRate(@PathVariable int tier, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        boolean isDeleted = electricityRateService.deleteElectricityRateUsingQueryDSL(tier);
        if (!isDeleted) {
            return new ResponseEntity<>("Electricity rate not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Electricity rate deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/find/{tier}")
    public ResponseEntity<?> getElectricityRateByTier(@PathVariable int tier) {
        List<ElectricityRate> rate = electricityRateService.getElectricityRateByTierUsingQueryDSL(tier);
        if (rate == null) {
            return new ResponseEntity<>("Electricity rate not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rate, HttpStatus.OK);
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> getAllElectricityRates() {
        List<ElectricityRate> rates = electricityRateService.getAllElectricityRateUsingQueryDSL();
        if (rates.isEmpty()) {
            return new ResponseEntity<>("No electricity rates found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }
}