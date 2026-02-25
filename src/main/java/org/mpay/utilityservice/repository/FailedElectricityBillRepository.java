package org.mpay.utilityservice.repository;

import org.mpay.utilityservice.entity.FailedElectricityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedElectricityBillRepository extends JpaRepository<FailedElectricityBill, Long> {
    // Used to fetch records for the Export Failed Report button
    List<FailedElectricityBill> findByJobId(Long jobId);
}