package com.example.electricitybill.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MANUFACTURE_ELECTRICITY_RATES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManufactureElectricityRate
{
    @Id
    private int tier;
    private double rate;

}
