package com.example.electricitybill.controller;
import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.service.ElectricityRateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/electricity-rate")
@AllArgsConstructor
public class ElectricityRateController {

    private final ElectricityRateService electricityRateService;

    @PostMapping("/add")
    public ResponseEntity<?> addElectricityRate(@RequestHeader("Authorization") String token, @RequestBody ElectricityRate electricityRate) {
        return electricityRateService.addElectricityRate(token, electricityRate);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateElectricityRate(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody ElectricityRate electricityRate) {
        return electricityRateService.updateElectricityRate(token, id, electricityRate);
    }

    @DeleteMapping("/delete/{tier}")
    public ResponseEntity<?> deleteElectricityRate(@PathVariable int tier, @RequestHeader("Authorization") String token) {
        return electricityRateService.deleteElectricityRate(token, tier);
    }

    @GetMapping("/find/{tier}")
    public ResponseEntity<?> getElectricityRateByTier(@PathVariable int tier) {
        return electricityRateService.getElectricityRateByTier(tier);
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> getAllElectricityRates() {
        return electricityRateService.getAllElectricityRates();
    }
}