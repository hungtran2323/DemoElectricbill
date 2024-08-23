package com.example.electricitybill.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "BILL_INFO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String phoneNumber;
    private Date entryDate;
    private int electricityConsumption;
    private double totalBill;


}
