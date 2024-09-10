package com.example.electricitybill.controller;

import com.example.electricitybill.model.BillInfo;
import com.example.electricitybill.service.BillInfoService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/bills")
@AllArgsConstructor
public class BillController {

    private final BillInfoService billService;

    @PostMapping("/add")
    public ResponseEntity<?> createBill(@RequestBody BillInfo billInfo, @RequestHeader("Authorization") String token) {
        return billService.createBill(billInfo, token);
    }

    @PutMapping("/update/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> updateBill(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate, @RequestBody BillInfo updatedBillInfo, @RequestHeader("Authorization") String token) {
        return billService.updateBill(phoneNumber, entryDate, updatedBillInfo, token);
    }

    @DeleteMapping("/delete/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> deleteBill(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date entryDate, @RequestHeader("Authorization") String token) {
        return billService.deleteBill(phoneNumber, entryDate, token);
    }

    @GetMapping("/find/{phoneNumber}")
    public ResponseEntity<?> getBillsByPhoneNumber(@PathVariable String phoneNumber) {
        return billService.getBillsByPhoneNumber(phoneNumber);
    }

    @GetMapping("/find/{phoneNumber}/{entryDate}")
    public ResponseEntity<?> getBillByPhoneNumberAndMonth(@PathVariable String phoneNumber, @PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date entryDate) {
        return billService.getBillByPhoneNumberAndMonth(phoneNumber, entryDate);
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> getAllBills(@RequestHeader("Authorization") String token) {
        return billService.getAllBills(token);
    }
}