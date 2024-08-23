package com.example.electricitybill.repository;

import com.example.electricitybill.model.BusinessElectricityRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessElectricityRateRepository extends JpaRepository<BusinessElectricityRate, Integer> {

    List<BusinessElectricityRate> findAllByOrderByTierAsc();
}
