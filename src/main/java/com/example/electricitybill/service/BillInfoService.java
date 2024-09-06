package com.example.electricitybill.service;

import com.example.electricitybill.model.BillInfo;
import com.example.electricitybill.repository.BillInfo.BillInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BillInfoService {

    @Autowired
    private BillInfoRepository billInfoRepository;

    public BillInfo saveBillInfo(BillInfo billInfo) {
        return billInfoRepository.save(billInfo);
    }

    @Transactional
    public BillInfo updateBillInfoUsingQueryDSL(String phoneNumber, Date entryDate, BillInfo updatedBillInfo) {
        return billInfoRepository.updateBillInfoUsingQueryDSL(phoneNumber, entryDate, updatedBillInfo);
    }

    @Transactional
    public boolean deleteBillInfoUsingQueryDSL(String phoneNumber, Date entryDate) {
        return billInfoRepository.deleteBillInfoUsingQueryDSL(phoneNumber, entryDate);
    }

    public List<BillInfo> getBillInfoByPhoneNumberUsingQueryDSL(String phoneNumber) {
        return billInfoRepository.getBillInfoByPhoneNumberUsingQueryDSL(phoneNumber);
    }

    public List<BillInfo> getBillInfoByPhoneNumberAndMonthUsingQueryDSL(String phoneNumber, Date entryDate) {
        return billInfoRepository.getBillInfoByPhoneNumberAndMonthUsingQueryDSL(phoneNumber, entryDate);
    }

    public List<BillInfo> getAllBillInfoUsingQueryDSL() {
        return billInfoRepository.getAllBillInfoUsingQueryDSL();
    }
}