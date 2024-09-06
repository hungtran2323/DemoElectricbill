package com.example.electricitybill.repository.BillInfo;

import com.example.electricitybill.model.BillInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BillInfoRepositoryImpl implements BillInfoRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
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

    @Override
    public boolean deleteBillInfoUsingQueryDSL(String phoneNumber, Date entryDate) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        long deletedCount = queryFactory.delete(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber)
                        .and(qBillInfo.entryDate.eq(entryDate)))
                .execute();

        return deletedCount > 0;
    }

    @Override
    public List<BillInfo> getBillInfoByPhoneNumberUsingQueryDSL(String phoneNumber) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        return queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber))
                .fetch();
    }

    @Override
    public List<BillInfo> getBillInfoByPhoneNumberAndMonthUsingQueryDSL(String phoneNumber, Date entryDate) {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        return queryFactory.selectFrom(qBillInfo)
                .where(qBillInfo.phoneNumber.eq(phoneNumber)
                        .and(qBillInfo.entryDate.eq(entryDate)))
                .fetch();
    }
    @Override
    public List<BillInfo> getAllBillInfoUsingQueryDSL() {
        QBillInfo qBillInfo = QBillInfo.billInfo;
        return queryFactory.selectFrom(qBillInfo)
                .fetch();
    }


}