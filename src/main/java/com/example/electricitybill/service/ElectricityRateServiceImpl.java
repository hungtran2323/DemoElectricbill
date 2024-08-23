package com.example.electricitybill.service;

import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.repository.ElectricityRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectricityRateServiceImpl implements ElectricityRateService {

    @Autowired
    private ElectricityRateRepository electricityRateRepository;

    @Override
    public ElectricityRate addElectricityRate(ElectricityRate electricityRate) {
        return electricityRateRepository.save(electricityRate);
    }

    @Override
    public ElectricityRate updateElectricityRate(ElectricityRate electricityRate) {
        return electricityRateRepository.save(electricityRate);
    }
}