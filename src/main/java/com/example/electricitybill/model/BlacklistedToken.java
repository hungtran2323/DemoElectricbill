package com.example.electricitybill.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table  (name = "BLACKLISTED_TOKENS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlacklistedToken {
    @Id
    private String token;
    private Date loginTime;
}