package com.example.electricitybill.service;

import com.example.electricitybill.model.ElectricityRate;

public interface ElectricityRateService {
    ElectricityRate addElectricityRate(ElectricityRate electricityRate);
    ElectricityRate updateElectricityRate(ElectricityRate electricityRate);
}