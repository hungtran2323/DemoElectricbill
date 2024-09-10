package com.example.electricitybill.service;

import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.repository.ElectricRate.ElectricityRateRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ElectricityRateService {

    private final ElectricityRateRepository electricityRateRepository;
    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n";

    public ResponseEntity<?> addElectricityRate(String token, ElectricityRate electricityRate) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        ElectricityRate newRate = electricityRateRepository.save(electricityRate);
        return new ResponseEntity<>(newRate, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateElectricityRate(String token, Integer id, ElectricityRate electricityRate) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        ElectricityRate updatedRate = electricityRateRepository.findById(id)
                .map(existingRate -> {
                    existingRate.setRate(electricityRate.getRate());
                    existingRate.setThreshold(electricityRate.getThreshold());
                    return electricityRateRepository.save(existingRate);
                })
                .orElseThrow(() -> new RuntimeException("Electricity rate not found"));

        return new ResponseEntity<>(updatedRate, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteElectricityRate(String token, int tier) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        boolean isDeleted = electricityRateRepository.deleteElectricityRateUsingQueryDSL(tier);
        if (!isDeleted) {
            return new ResponseEntity<>("Electricity rate not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Electricity rate deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<?> getElectricityRateByTier(int tier) {
        List<ElectricityRate> rate = electricityRateRepository.getElectricityRateByTierUsingQueryDSL(tier);
        if (rate == null || rate.isEmpty()) {
            return new ResponseEntity<>("Electricity rate not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rate, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllElectricityRates() {
        List<ElectricityRate> rates = electricityRateRepository.getAllElectricityRatesUsingQueryDSL();
        if (rates.isEmpty()) {
            return new ResponseEntity<>("No electricity rates found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }
}