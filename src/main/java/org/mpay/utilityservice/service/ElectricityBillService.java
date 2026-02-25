package org.mpay.utilityservice.service;

import lombok.RequiredArgsConstructor;
import org.mpay.utilityservice.entity.ElectricityBill;
import org.mpay.utilityservice.entity.FailedElectricityBill;
import org.mpay.utilityservice.repository.ElectricityBillRepository;
import org.mpay.utilityservice.repository.FailedElectricityBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricityBillService {

    private final ElectricityBillRepository repository;
    private final FailedElectricityBillRepository failedRepository;

    // Use REQUIRES_NEW to ensure each save is its own atomic unit
    // This prevents one fail from rolling back the whole chunk.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSingleEntity(ElectricityBill entity) {
        repository.saveAndFlush(entity);
    }

    @Transactional
    public void saveFailedBills(List<FailedElectricityBill> failedEntities) {
        if (!failedEntities.isEmpty()) {
            failedRepository.saveAll(failedEntities);
        }
    }

    public List<FailedElectricityBill> getFailedBillsByJobId(Long jobId) {
        return failedRepository.findByJobId(jobId);
    }
}