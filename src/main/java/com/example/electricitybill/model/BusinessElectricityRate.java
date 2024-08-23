package com.example.electricitybill.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BUSINESS_ELECTRICITY_RATES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BusinessElectricityRate {
    @Id
    private int tier;
    private double rate;
}
