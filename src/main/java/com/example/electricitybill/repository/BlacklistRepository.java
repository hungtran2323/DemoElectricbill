package com.example.electricitybill.repository;

import com.example.electricitybill.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistedToken, String> {
    void deleteByToken(String token);
}