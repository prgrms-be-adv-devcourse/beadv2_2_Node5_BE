package com.node5.batchservice.subscription.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@ConditionalOnProperty(name = "batch.scheduler.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SubscriptionOrderJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;

    @Scheduled(cron = "${spring.task.scheduling.cron.subscription-order}")
    public void runSubscriptionOrderJob() {
        LocalDate today = LocalDate.now();
        log.info("Running subscription order for date {}", today);
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runDate", today.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(subscriptionOrderJob, params);
        } catch (Exception ex) {
            log.error("Failed to launch subscription batch: {}", ex.getMessage(), ex);
        }
    }
}
