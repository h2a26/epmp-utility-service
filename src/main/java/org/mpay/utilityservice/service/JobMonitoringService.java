package org.mpay.utilityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mpay.utilityservice.dto.JobExecutionInfo;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobMonitoringService {

    private final JobExplorer jobExplorer;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public List<JobExecutionInfo> getRecentJobExecutions(int limit) {
        try {
            return jobExplorer.findJobInstancesByJobName("importElectricityBillJob", 0, limit)
                    .stream()
                    .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                    .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                    .limit(limit)
                    .map(this::mapToJobExecutionInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching recent job executions", e);
            return List.of();
        }
    }

    private JobExecutionInfo mapToJobExecutionInfo(JobExecution jobExecution) {
        String fileName = jobExecution.getJobParameters().getString("originalFileName");
        if (fileName == null) fileName = "Unknown";

        long totalSuccess = 0;
        long totalFailed = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            totalSuccess += stepExecution.getWriteCount();

            totalFailed += stepExecution.getFilterCount()
                    + stepExecution.getProcessSkipCount()
                    + stepExecution.getWriteSkipCount();
        }

        String startTime = jobExecution.getStartTime() != null
                ? jobExecution.getStartTime().format(FORMATTER)
                : "N/A";

        String endTime = jobExecution.getEndTime() != null
                ? jobExecution.getEndTime().format(FORMATTER)
                : "N/A";

        return JobExecutionInfo.builder()
                .jobId(jobExecution.getId())
                .fileName(fileName)
                .status(jobExecution.getStatus().name())
                .successCount(totalSuccess)
                .failedCount(totalFailed)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}