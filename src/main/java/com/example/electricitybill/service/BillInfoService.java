package com.example.electricitybill.service;

import com.example.electricitybill.model.BillInfo;
import com.example.electricitybill.repository.BillInfoRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

@Service
public class BillInfoService {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BillInfoRepository billInfoRepository;

    public BillInfo saveBillInfo(BillInfo billInfo) {
        return billInfoRepository.save(billInfo);
    }

    @Transactional
    public BillInfo updateBillInfoUsingQueryDSL(String phoneNumber, Date entryDate, BillInfo updatedBillInfo) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        List<BillInfo> existingBills = queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber)
                        .and(qBillInfo.entryDate.eq(entryDate)))
                .fetch();

        if (existingBills.isEmpty()) {
            return null;
        }

        BillInfo billToUpdate = existingBills.get(0);

        queryFactory.update(qBillInfo)
                .where(qBillInfo.id.eq(billToUpdate.getId()))
                .set(qBillInfo.entryDate, updatedBillInfo.getEntryDate())
                .set(qBillInfo.electricityConsumption, updatedBillInfo.getElectricityConsumption())
                .set(qBillInfo.totalBill, updatedBillInfo.getTotalBill())
                .execute();

        return queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.id.eq(billToUpdate.getId()))
                .fetchOne();
    }

    @Transactional
    public boolean deleteBillInfoUsingQueryDSL(String phoneNumber, Date entryDate) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        long deletedCount = queryFactory.delete(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber)
                        .and(qBillInfo.entryDate.eq(entryDate)))
                .execute();

        return deletedCount > 0;
    }

    public List<BillInfo> getBillInfoByPhoneNumberUsingQueryDSL(String phoneNumber) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        return queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber))
                .fetch();
    }

    public List<BillInfo> getBillInfoByPhoneNumberAndMonthUsingQueryDSL(String phoneNumber, Date entryDate) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        return queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber)
                        .and(qBillInfo.entryDate.eq(entryDate)))
                .fetch();
    }
}