package com.example.electricitybill.repository;

import com.example.electricitybill.model.BusinessElectricityRate;
import com.example.electricitybill.model.ElectricityRate;
import com.example.electricitybill.model.ManufactureElectricityRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManufactureElectricityRateRepository extends JpaRepository<ManufactureElectricityRate, Integer> {
    List<BusinessElectricityRate> findAllByOrderByTierAsc();
}
