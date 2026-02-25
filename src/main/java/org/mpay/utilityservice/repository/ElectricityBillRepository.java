package org.mpay.utilityservice.repository;

import org.mpay.utilityservice.entity.ElectricityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricityBillRepository extends JpaRepository<ElectricityBill, Long> {
}