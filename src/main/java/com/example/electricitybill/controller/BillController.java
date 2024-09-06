package com.example.electricitybill.controller;

import com.example.electricitybill.model.*;
import com.example.electricitybill.repository.*;
import com.example.electricitybill.repository.BillInfo.BillInfoRepository;
import com.example.electricitybill.repository.ElectricRate.ElectricityRateRepository;
import com.example.electricitybill.repository.User.UserRepository;
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
    private final BillInfoRepository billInfoRepository;
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

        List<BillInfo> existingBills = billInfoService.getBillInfoByPhoneNumberAndMonthUsingQueryDSL(billInfo.getPhoneNumber(), billInfo.getEntryDate());
        if (!existingBills.isEmpty()) {
            return new ResponseEntity<>("A bill with the same phone number and month already exists", HttpStatus.CONFLICT);
        }

        User user = userOptional.get();
        double totalBill;
        switch (user.getType()) {
            case "business":
                totalBill = calculateBusinessTotalBill(billInfo.getElectricityConsumption());
                break;
            case "manufacture":
                totalBill = calculateManufactureTotalBill(billInfo.getElectricityConsumption());
                break;
            default:
                totalBill = calculateTotalBill(billInfo.getElectricityConsumption());
                break;
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

        BillInfo billToUpdate = billInfoService.updateBillInfoUsingQueryDSL(phoneNumber, entryDate, updatedBillInfo);
        if (billToUpdate == null) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

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

        boolean isDeleted = billInfoService.deleteBillInfoUsingQueryDSL(phoneNumber, entryDate);
        if (!isDeleted) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Bill deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/find/{phoneNumber}")
    public ResponseEntity<?> getBillsByPhoneNumber(@PathVariable String phoneNumber) {
        List<BillInfo> bills = billInfoService.getBillInfoByPhoneNumberUsingQueryDSL(phoneNumber);
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found with the provided phone number", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/find/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> getBillByPhoneNumberAndMonth(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date entryDate) {
        List<BillInfo> bills = billInfoService.getBillInfoByPhoneNumberAndMonthUsingQueryDSL(phoneNumber, entryDate);
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

        List<BillInfo> bills = billInfoService.getAllBillInfoUsingQueryDSL();
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

}