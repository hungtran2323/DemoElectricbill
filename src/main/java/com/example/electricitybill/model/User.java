package com.example.electricitybill.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    private int id;

    @Column(length = 20)
    private String phoneNumber;
    private String email;
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "type")
    private String type;


}
