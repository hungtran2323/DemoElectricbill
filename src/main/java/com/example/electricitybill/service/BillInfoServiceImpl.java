package com.example.electricitybill.service;

import com.example.electricitybill.model.BillInfo;
import com.example.electricitybill.repository.BillInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BillInfoServiceImpl implements BillInfoService {

    @Autowired
    private BillInfoRepository billInfoRepository;

    @Override
    public List<BillInfo> getBillInfoByPhoneNumberAndMonth(String phoneNumber, Date entryDate) {
        return billInfoRepository.findByPhoneNumberAndMonth(phoneNumber, entryDate);
    }

    @Override
    public List<BillInfo> getBillInfoByPhoneNumber(String phoneNumber) {
        return billInfoRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public BillInfo saveBillInfo(BillInfo billInfo) {
        return billInfoRepository.save(billInfo);
    }

    @Override
    public void deleteBillInfo(BillInfo billInfo) {
        billInfoRepository.delete(billInfo);
    }

    @Override
    public List<BillInfo> getAllBillInfo() {
        return billInfoRepository.findAll();
    }
}