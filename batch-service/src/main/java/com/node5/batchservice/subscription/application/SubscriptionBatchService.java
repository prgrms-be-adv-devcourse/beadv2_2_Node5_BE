package com.node5.batchservice.subscription.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionBatchService {
    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;

    public void runBatch(LocalDate date) {
        LocalDate runDate = date != null ? date : LocalDate.now();
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runDate", runDate.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(subscriptionOrderJob, params);
        } catch (Exception ex) {
            log.error("Failed to launch subscription batch: {}", ex.getMessage(), ex);
        }
    }
}
