package org.mpay.utilityservice.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class FileCleanupListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Retrieve the local path from the job parameters
        String localPath = jobExecution.getJobParameters().getString("localFilePath");

        if (localPath != null) {
            try {
                Path path = Paths.get(localPath);
                boolean deleted = Files.deleteIfExists(path);

                if (deleted) {
                    log.info("Successfully deleted local temp file: {}", localPath);
                } else {
                    log.warn("Attempted to delete temp file, but it did not exist: {}", localPath);
                }
            } catch (Exception e) {
                log.error("Failed to delete local temp file at: {}. Manual cleanup may be required. Error: {}",
                        localPath, e.getMessage());
            }
        }
    }
}