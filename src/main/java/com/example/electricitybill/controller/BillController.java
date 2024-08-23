package com.example.electricitybill.controller;

import com.example.electricitybill.model.*;
import com.example.electricitybill.repository.BusinessElectricityRateRepository;
import com.example.electricitybill.repository.ElectricityRateRepository;
import com.example.electricitybill.repository.ManufactureElectricityRateRepository;
import com.example.electricitybill.repository.UserRepository;
import com.example.electricitybill.service.BillInfoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@AllArgsConstructor
public class BillController {

    private final UserRepository userRepository;
    private final BillInfoService billInfoService;
    private final ElectricityRateRepository electricityRateRepository;
    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n";
    private final BusinessElectricityRateRepository businessElectricityRateRepository;
    private final ManufactureElectricityRateRepository manufactureElectricityRateRepository;

    private double calculateTotalBill(int electricityConsumption) {
        List<ElectricityRate> rates = electricityRateRepository.findAllByOrderByTierAsc();

        double totalBill = 0;
        int remainingConsumption = electricityConsumption;

        for (ElectricityRate rate : rates) {
            if (remainingConsumption <= 0) {
                break;
            }

            int consumptionForThisTier = Math.min(remainingConsumption, rate.getThreshold());
            totalBill += consumptionForThisTier * rate.getRate();

            remainingConsumption -= consumptionForThisTier;
        }

        if (remainingConsumption > 0) {
            totalBill += remainingConsumption * rates.get(rates.size() - 1).getRate();
        }

        return totalBill;
    }

    private double calculateBusinessTotalBill(int electricityConsumption) {
        BusinessElectricityRate rate = businessElectricityRateRepository.findById(1).orElseThrow(() -> new RuntimeException("Rate not found"));
        double totalBill = electricityConsumption * rate.getRate();
        return totalBill;
    }

    private double calculateManufactureTotalBill(int electricityConsumption) {
        ManufactureElectricityRate rate = manufactureElectricityRateRepository.findById(1).orElseThrow(() -> new RuntimeException("Rate not found"));
        double totalBill = electricityConsumption * rate.getRate();
        return totalBill;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createBill(@RequestBody BillInfo billInfo, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(billInfo.getPhoneNumber());

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        List<BillInfo> existingBills = billInfoService.getBillInfoByPhoneNumberAndMonth(billInfo.getPhoneNumber(), billInfo.getEntryDate());
        if (!existingBills.isEmpty()) {
            return new ResponseEntity<>("A bill with the same phone number and month already exists", HttpStatus.CONFLICT);
        }

        User user = userOptional.get();
        double totalBill;
        if ("business".equals(user.getType())) {
            totalBill = calculateBusinessTotalBill(billInfo.getElectricityConsumption());
        } else if ("manufacture".equals(user.getType())) {
            totalBill = calculateManufactureTotalBill(billInfo.getElectricityConsumption());
        } else {
            totalBill = calculateTotalBill(billInfo.getElectricityConsumption());
        }

        billInfo.setTotalBill(totalBill);
        billInfo.setEntryDate(new Date());

        BillInfo savedBillInfo = billInfoService.saveBillInfo(billInfo);

        return new ResponseEntity<>(savedBillInfo, HttpStatus.CREATED);
    }

    @PutMapping("/update/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> updateBill(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate, @RequestBody BillInfo updatedBillInfo, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<BillInfo> existingBills = billInfoService.getBillInfoByPhoneNumberAndMonth(phoneNumber, entryDate);
        if (existingBills.isEmpty()) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

        BillInfo billToUpdate = existingBills.get(0);

        billToUpdate.setEntryDate(updatedBillInfo.getEntryDate());
        billToUpdate.setElectricityConsumption(updatedBillInfo.getElectricityConsumption());

        double totalBill = calculateTotalBill(billToUpdate.getElectricityConsumption());
        billToUpdate.setTotalBill(totalBill);

        BillInfo savedBillInfo = billInfoService.saveBillInfo(billToUpdate);

        return new ResponseEntity<>(savedBillInfo, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> deleteBill(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate, @RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<BillInfo> existingBills = billInfoService.getBillInfoByPhoneNumberAndMonth(phoneNumber, entryDate);
        if (existingBills.isEmpty()) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

        BillInfo billToDelete = existingBills.get(0);
        billInfoService.deleteBillInfo(billToDelete);
        return new ResponseEntity<>("Bill deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/find/{phoneNumber}")
    public ResponseEntity<?> getBillsByPhoneNumber(@PathVariable String phoneNumber) {
        List<BillInfo> bills = billInfoService.getBillInfoByPhoneNumber(phoneNumber);
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found with the provided phone number", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/find/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> getBillByPhoneNumberAndMonth(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date entryDate) {
        List<BillInfo> bills = billInfoService.getBillInfoByPhoneNumberAndMonth(phoneNumber, entryDate);
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> getAllBills(@RequestHeader("Authorization") String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<BillInfo> bills = billInfoService.getAllBillInfo();
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

}