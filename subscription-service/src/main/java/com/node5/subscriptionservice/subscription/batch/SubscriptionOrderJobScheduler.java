package com.node5.subscriptionservice.subscription.batch;

import com.node5.subscriptionservice.subscription.domain.SubscriptionRepository;
import com.node5.subscriptionservice.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionOrderJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "${spring.task.}")
    public void runSubscriptionOrderJob() {
        LocalDate today = LocalDate.now();
        log.info("Running subscription order for date {}", today);
        Pageable pageable = Pageable.ofSize(100);
        Page<UUID> page;

        do {
            page = subscriptionRepository.findAllByNextRunDateAndSubscriptionStatus(today, SubscriptionStatus.ACTIVE, pageable)
                    .map(subscription -> subscription.getId());

            List<UUID> subscriptionIds = page.getContent();

            if (subscriptionIds.isEmpty()) {
                break;
            }

            log.info("Processing page chunk for {} subscriptions (page {}/{})",
                    subscriptionIds.size(), page.getNumber() + 1, page.getTotalPages());

            subscriptionIds.forEach(subscriptionId ->{
                try {
                    JobParameters params = new JobParametersBuilder()
                            .addString("subscriptionId", subscriptionId.toString())
                            .addString("runDate", today.toString())
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters();
                    jobLauncher.run(subscriptionOrderJob, params);
                    log.info("Triggered subscription job for {}", subscriptionId);
                } catch (Exception ex) {
                    log.error("Failed to trigger job for {}: {}", subscriptionId, ex.getMessage(), ex);
                }
            });

            pageable = page.hasNext() ? page.nextPageable() : Pageable.unpaged();
        } while (page.hasNext());
    }
}
