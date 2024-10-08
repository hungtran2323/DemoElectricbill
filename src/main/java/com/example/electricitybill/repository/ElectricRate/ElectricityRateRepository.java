package com.example.electricitybill.repository.ElectricRate;

import com.example.electricitybill.model.ElectricityRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectricityRateRepository extends JpaRepository<ElectricityRate, Integer>, ElectricityRateRepositoryCustom {
    List<ElectricityRate> findAllByOrderByTierAsc();
}