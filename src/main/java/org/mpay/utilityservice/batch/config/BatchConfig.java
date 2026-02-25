package org.mpay.utilityservice.batch.config;

import org.mpay.utilityservice.batch.listener.FileCleanupListener;
import org.mpay.utilityservice.batch.processor.ElectricityBillProcessor;
import org.mpay.utilityservice.batch.writer.ElectricityBillWriter;
import org.mpay.utilityservice.dto.BillValidationResult;
import org.mpay.utilityservice.dto.ElectricityBillRaw;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Value("${spring-batch.chunk-size:100}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FileCleanupListener fileCleanupListener;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, FileCleanupListener fileCleanupListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.fileCleanupListener = fileCleanupListener;
    }

    @Bean
    public Job importElectricityBillJob(Step importBillStep) {
        return new JobBuilder("importElectricityBillJob", jobRepository)
                .listener(fileCleanupListener)
                .start(importBillStep)
                .build();
    }

    @Bean
    public Step importBillStep(
            PoiItemReader<ElectricityBillRaw> excelBillReader,
            ElectricityBillProcessor processor,
            ElectricityBillWriter writer) {

        // FIX: Wrap the non-thread-safe PoiItemReader to make it safe for multi-threading
        SynchronizedItemStreamReader<ElectricityBillRaw> synchronizedReader =
                new SynchronizedItemStreamReaderBuilder<ElectricityBillRaw>()
                        .delegate(excelBillReader)
                        .build();

        return new StepBuilder("importBillStep", jobRepository)
                .<ElectricityBillRaw, BillValidationResult>chunk(chunkSize, transactionManager)
                .reader(synchronizedReader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Optimized for 1000 records / 100 chunk size = 10 chunks
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("BillBatch-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "asyncJobLauncher")
    public JobLauncher getJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}