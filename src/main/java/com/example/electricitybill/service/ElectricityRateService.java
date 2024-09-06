package com.example.electricitybill.service;

import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.repository.ElectricRate.ElectricityRateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ElectricityRateService {

    @Autowired
    private ElectricityRateRepository electricityRateRepository;

    public ElectricityRate saveElectricityRate(ElectricityRate electricityRate) {
        return electricityRateRepository.save(electricityRate);
    }

    @Transactional
    public ElectricityRate updateElectricityRateUsingQueryDSL(Integer id, ElectricityRate updatedElectricityRate) {
        return electricityRateRepository.updateElectricityRateUsingQueryDSL(id, updatedElectricityRate);
    }

    @Transactional
    public boolean deleteElectricityRateUsingQueryDSL(Integer id) {
        return electricityRateRepository.deleteElectricityRateUsingQueryDSL(id);
    }

    public List<ElectricityRate> getElectricityRateByTierUsingQueryDSL(Integer tier) {
        return electricityRateRepository.getElectricityRateByTierUsingQueryDSL(tier);
    }

    public List<ElectricityRate> getElectricityRateByDateUsingQueryDSL(Date effectiveDate) {
        return electricityRateRepository.getElectricityRateByDateUsingQueryDSL(effectiveDate);
    }

    public List<ElectricityRate> getAllElectricityRateUsingQueryDSL() {
        return electricityRateRepository.getAllElectricityRateUsingQueryDSL();
    }
}