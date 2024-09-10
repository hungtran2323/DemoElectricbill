package com.example.electricitybill.repository.BillInfo;

import com.example.electricitybill.model.BillInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillInfoRepository extends JpaRepository<BillInfo, Integer>, BillInfoRepositoryCustom {
    List<BillInfo> findByPhoneNumber(String phoneNumber);
    BillInfo saveBillInfo(BillInfo billToUpdate);
}