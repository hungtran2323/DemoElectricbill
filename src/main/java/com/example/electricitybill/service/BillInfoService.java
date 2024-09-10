package com.example.electricitybill.service;

import com.example.electricitybill.model.*;
import com.example.electricitybill.repository.*;
import com.example.electricitybill.repository.BillInfo.BillInfoRepository;
import com.example.electricitybill.repository.User.UserRepository;
import com.example.electricitybill.repository.ElectricRate.ElectricityRateRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BillInfoService {

    private final UserRepository userRepository;
    private final BillInfoService billInfoService;
    private final BillInfoRepository billInfoRepository;
    private final ElectricityRateRepository electricityRateRepository;
    private final String signingKey = "g3+xiQuEw1YWXRe/AwIBGCxYNbrA+VpiWM0HQdk/VJRTYPKzaUJsGIcOKZlWgcaK\n";
    private final BusinessElectricityRateRepository businessElectricityRateRepository;
    private final ManufactureElectricityRateRepository manufactureElectricityRateRepository;
    private final JPAQueryFactory queryFactory;


    public double calculateTotalBill(int electricityConsumption) {
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

    public double calculateBusinessTotalBill(int electricityConsumption) {
        BusinessElectricityRate rate = businessElectricityRateRepository.findById(1).orElseThrow(() -> new RuntimeException("Rate not found"));
        double totalBill = electricityConsumption * rate.getRate();
        return totalBill;
    }

    public double calculateManufactureTotalBill(int electricityConsumption) {
        ManufactureElectricityRate rate = manufactureElectricityRateRepository.findById(1).orElseThrow(() -> new RuntimeException("Rate not found"));
        double totalBill = electricityConsumption * rate.getRate();
        return totalBill;
    }

    public ResponseEntity<?> createBill(BillInfo billInfo, String token) {
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

        List<BillInfo> existingBills = billInfoRepository.getBillInfoByPhoneNumberAndMonthUsingQueryDSL(billInfo.getPhoneNumber(), billInfo.getEntryDate());
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

        BillInfo savedBillInfo = billInfoRepository.saveBillInfo(billInfo);

        return new ResponseEntity<>(savedBillInfo, HttpStatus.CREATED);
    }


    public ResponseEntity<?> updateBill(String phoneNumber, Date entryDate, BillInfo updatedBillInfo, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        BillInfo billToUpdate = billInfoRepository.updateBillInfoUsingQueryDSL(phoneNumber, entryDate, updatedBillInfo);
        if (billToUpdate == null) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

        double totalBill = calculateTotalBill(billToUpdate.getElectricityConsumption());
        billToUpdate.setTotalBill(totalBill);

        BillInfo savedBillInfo = billInfoRepository.saveBillInfo(billToUpdate);

        return new ResponseEntity<>(savedBillInfo, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteBill(String phoneNumber, Date entryDate, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        boolean isDeleted = billInfoRepository.deleteBillInfoUsingQueryDSL(phoneNumber, entryDate);
        if (!isDeleted) {
            return new ResponseEntity<>("No bill found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Bill deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<?> getBillsByPhoneNumber(String phoneNumber) {
        List<BillInfo> bills = billInfoRepository.getBillInfoByPhoneNumberUsingQueryDSL(phoneNumber);
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found with the provided phone number", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    public ResponseEntity<?> getBillByPhoneNumberAndMonth(String phoneNumber, Date entryDate) {
        List<BillInfo> bills = billInfoRepository.getBillInfoByPhoneNumberAndMonthUsingQueryDSL(phoneNumber, entryDate);
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found with the provided phone number and entry date", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllBills(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String role = (String) claims.get("role");

        if (!"admin".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<BillInfo> bills = billInfoRepository.getAllBillInfoUsingQueryDSL();
        if (bills.isEmpty()) {
            return new ResponseEntity<>("No bills found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }
}