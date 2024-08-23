package com.example.electricitybill.service;

import com.example.electricitybill.model.BillInfo;

import java.util.Date;
import java.util.List;

public interface BillInfoService {
    List<BillInfo> getBillInfoByPhoneNumberAndMonth(String phoneNumber, Date entryDate);
    List<BillInfo> getBillInfoByPhoneNumber(String phoneNumber);
    BillInfo saveBillInfo(BillInfo billInfo);
    void deleteBillInfo(BillInfo billInfo);
    List<BillInfo> getAllBillInfo(); // Add this method
}