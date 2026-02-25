package org.mpay.utilityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobMaintenanceService {

    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository; // Need Repository to update status

    @EventListener(ApplicationReadyEvent.class)
    public void cleanupStuckJobs() {
        log.info("Checking for stuck 'STARTED' jobs from previous sessions...");

        // Find all running executions for your specific job name
        Set<JobExecution> runningExecutions = jobExplorer.findRunningJobExecutions("importElectricityBillJob");

        for (JobExecution execution : runningExecutions) {
            log.warn("Found stuck job execution ID {}. Marking as FAILED.", execution.getId());

            // Set the status to FAILED so the UI badge stops spinning
            execution.setStatus(BatchStatus.FAILED);

            // Set an ExitStatus so the Batch metadata reflects the reason
            execution.setExitStatus(ExitStatus.FAILED.addExitDescription("System restart detected."));

            // Don't forget to set the end time, or your duration logic might break
            execution.setEndTime(LocalDateTime.now());

            // Corrected: Pass the instance 'execution'
            jobRepository.update(execution);
        }
    }
}