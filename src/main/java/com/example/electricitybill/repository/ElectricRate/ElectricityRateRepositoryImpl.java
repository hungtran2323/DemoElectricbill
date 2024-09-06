package com.example.electricitybill.repository.ElectricRate;

import com.example.electricitybill.model.ElectricityRate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ElectricityRateRepositoryImpl implements ElectricityRateRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public ElectricityRate updateElectricityRateUsingQueryDSL(Integer id, ElectricityRate updatedElectricityRate) {
        QElectricityRate qElectricityRate = QElectricityRate.electricityRate;

        ElectricityRate existingRate = queryFactory.selectFrom(qElectricityRate)
                .where(qElectricityRate.id.eq(id))
                .fetchOne();

        if (existingRate == null) {
            return null;
        }

        queryFactory.update(qElectricityRate)
                .where(qElectricityRate.id.eq(id))
                .set(qElectricityRate.tier, updatedElectricityRate.getTier())
                .set(qElectricityRate.rate, updatedElectricityRate.getRate())
                .set(qElectricityRate.effectiveDate, updatedElectricityRate.getEffectiveDate())
                .execute();

        return queryFactory.selectFrom(qElectricityRate)
                .where(qElectricityRate.id.eq(id))
                .fetchOne();
    }

    @Override
    public boolean deleteElectricityRateUsingQueryDSL(Integer id) {
        QElectricityRate qElectricityRate = QElectricityRate.electricityRate;

        long deletedCount = queryFactory.delete(qElectricityRate)
                .where(qElectricityRate.id.eq(id))
                .execute();

        return deletedCount > 0;
    }

    @Override
    public List<ElectricityRate> getElectricityRateByTierUsingQueryDSL(Integer tier) {
        QElectricityRate qElectricityRate = QElectricityRate.electricityRate;

        return queryFactory.selectFrom(qElectricityRate)
                .where(qElectricityRate.tier.eq(tier))
                .fetch();
    }

    @Override
    public List<ElectricityRate> getElectricityRateByDateUsingQueryDSL(Date effectiveDate) {
        QElectricityRate qElectricityRate = QElectricityRate.electricityRate;

        return queryFactory.selectFrom(qElectricityRate)
                .where(qElectricityRate.effectiveDate.eq(effectiveDate))
                .fetch();
    }


    @Override
    public List<ElectricityRate> getAllElectricityRatesUsingQueryDSL() {
        QElectricityRate electricityRate = QElectricityRate.electricityRate;
        return queryFactory.selectFrom(electricityRate).
                fetch();
    }

}