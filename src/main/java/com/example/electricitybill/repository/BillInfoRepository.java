package com.example.electricitybill.repository;

import com.example.electricitybill.model.BillInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BillInfoRepository extends JpaRepository<BillInfo, Integer>  {
    @Query("SELECT b FROM BillInfo b WHERE b.phoneNumber = :phoneNumber AND FUNCTION('YEAR', b.entryDate) = FUNCTION('YEAR', :entryDate) AND FUNCTION('MONTH', b.entryDate) = FUNCTION('MONTH', :entryDate)")
    List<BillInfo> findByPhoneNumberAndMonth(@Param("phoneNumber") String phoneNumber, @Param("entryDate") Date entryDate);
    List<BillInfo> findByPhoneNumber(String phoneNumber);
}
