package com.example.electricitybill.repository.BillInfo;

import com.example.electricitybill.model.BillInfo;

import java.util.Date;
import java.util.List;

public interface BillInfoRepositoryCustom {
    BillInfo updateBillInfoUsingQueryDSL(String phoneNumber, Date entryDate, BillInfo updatedBillInfo);
    boolean deleteBillInfoUsingQueryDSL(String phoneNumber, Date entryDate);
    List<BillInfo> getBillInfoByPhoneNumberUsingQueryDSL(String phoneNumber);
    List<BillInfo> getBillInfoByPhoneNumberAndMonthUsingQueryDSL(String phoneNumber, Date entryDate);
    List<BillInfo> getAllBillInfoUsingQueryDSL();
}