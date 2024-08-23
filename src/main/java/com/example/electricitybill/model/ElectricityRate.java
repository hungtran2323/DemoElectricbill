package com.example.electricitybill.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ELECTRICITY_RATES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ElectricityRate {
    @Id
    private int tier;
    private int threshold;
    private double rate;


}
