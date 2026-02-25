package org.mpay.utilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionInfo {
    private Long jobId;
    private String fileName;
    private String status;
    private Long successCount;
    private Long failedCount;
    private String startTime;
    private String endTime;
}
