package com.node5.subscriptionservice.subscription.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionOrderJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void runSubscriptionOrderJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("scheduledAt", LocalDateTime.now())
                .toJobParameters();

        try {
            jobLauncher.run(subscriptionOrderJob, jobParameters);
            log.info("subscriptionOrderJob executed at {}", jobParameters.getParameters().get("scheduledAt"));
        } catch (Exception e) {
            log.error("Failed to execute subscriptionOrderJob: {}", e.getMessage(), e);
        }
    }
}
