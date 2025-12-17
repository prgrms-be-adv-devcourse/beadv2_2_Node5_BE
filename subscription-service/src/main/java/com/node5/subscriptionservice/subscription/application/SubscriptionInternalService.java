package com.node5.subscriptionservice.subscription.application;

import com.node5.subscriptionservice.subscription.domain.SubscriptionRepository;
import com.node5.subscriptionservice.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionInternalService {
    private final JobLauncher jobLauncher;
    private final Job subscriptionOrderJob;
    private final SubscriptionRepository subscriptionRepository;

    public void runBatch(LocalDate date) {
        LocalDate runDate = date != null ? date : LocalDate.now();
        Pageable pageable = Pageable.ofSize(100);
        Page<UUID> page;

        do {
            page = subscriptionRepository.findAllByNextRunDateAndSubscriptionStatus(runDate, SubscriptionStatus.ACTIVE, pageable)
                    .map(subscription -> subscription.getId());

            List<UUID> subscriptionIds = page.getContent();

            if (subscriptionIds.isEmpty()) {
                log.info("No subscriptions found for date {}", runDate);
                break;
            }

            log.info("Processing page chunk for {} subscriptions (page {}/{})",
                    subscriptionIds.size(), page.getNumber() + 1, page.getTotalPages());

            subscriptionIds.forEach(subscriptionId ->{
                try {
                    JobParameters params = new JobParametersBuilder()
                            .addString("subscriptionId", subscriptionId.toString())
                            .addString("runDate", runDate.toString())
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
