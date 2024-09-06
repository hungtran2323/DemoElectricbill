package com.example.electricitybill.repository.ElectricRate;

import com.example.electricitybill.model.ElectricityRate;

import java.util.Date;
import java.util.List;

public interface ElectricityRateRepositoryCustom {
    ElectricityRate updateElectricityRateUsingQueryDSL(Integer id, ElectricityRate updatedElectricityRate);
    boolean deleteElectricityRateUsingQueryDSL(Integer id);
    List<ElectricityRate> getElectricityRateByTierUsingQueryDSL(Integer tier);
    List<ElectricityRate> getElectricityRateByDateUsingQueryDSL(Date effectiveDate);
    List<ElectricityRate> getAllElectricityRatesUsingQueryDSL();

    List<ElectricityRate> getAllElectricityRateUsingQueryDSL();
}