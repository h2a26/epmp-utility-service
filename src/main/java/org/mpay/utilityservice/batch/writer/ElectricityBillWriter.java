package org.mpay.utilityservice.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mpay.utilityservice.dto.BillValidationResult;
import org.mpay.utilityservice.entity.FailedElectricityBill;
import org.mpay.utilityservice.service.ElectricityBillService;
import org.mpay.utilityservice.util.BillMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class ElectricityBillWriter implements ItemWriter<BillValidationResult> {

    private final ElectricityBillService billService;
    private StepExecution stepExecution;

    @Value("#{stepExecution.jobExecution.id}")
    private Long jobId;

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends BillValidationResult> chunk) {
        ConcurrentLinkedQueue<BillValidationResult> failureQueue = new ConcurrentLinkedQueue<>();
        LongAdder successCounter = new LongAdder();
        LongAdder failureCounter = new LongAdder();

        chunk.getItems().parallelStream().forEach(result -> {
            if (result.isValid()) {
                try {
                    billService.saveSingleEntity(result.validatedEntity());
                    successCounter.increment();
                } catch (Exception e) {
                    String shortError = translateException(e);
                    log.error("Job {} | Row {}: {}", jobId, result.rawData().rowNumber(), shortError);

                    failureQueue.add(new BillValidationResult(
                            result.rawData(),
                            null,
                            false,
                            shortError
                    ));
                    failureCounter.increment();
                }
            } else {
                failureQueue.add(result);
                failureCounter.increment();
            }
        });

        if (!failureQueue.isEmpty()) {
            List<FailedElectricityBill> failedEntities = failureQueue.stream()
                    .map(r -> BillMapper.mapToFailedEntity(r, jobId))
                    .toList();
            billService.saveFailedBills(failedEntities);
        }

        long currentFilterCount = stepExecution.getFilterCount();
        stepExecution.setFilterCount(currentFilterCount + failureCounter.sum());

        log.info(">>> Job ID: {} | Chunk Success: {} | Chunk Failed: {} | Total Written: {} | Total Filtered: {}",
                jobId, successCounter.sum(), failureCounter.sum(),
                stepExecution.getWriteCount(), stepExecution.getFilterCount());
    }

    private String translateException(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("uq_consumer_area") || (msg.contains("consumer_no") && msg.contains("area"))) {
                return "Duplicate: Consumer already exists in this area.";
            }
            if (msg.contains("not-null") || msg.contains("null value")) {
                return "DB Error: Missing required data fields.";
            }
            return "Database Integrity Error: Constraint violation.";
        }
        return "System Error: Unexpected database failure.";
    }
}