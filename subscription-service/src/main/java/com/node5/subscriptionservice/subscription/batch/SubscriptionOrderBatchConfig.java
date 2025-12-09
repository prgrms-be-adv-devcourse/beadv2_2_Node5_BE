package com.node5.subscriptionservice.subscription.batch;

import com.node5.subscriptionservice.subscription.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SubscriptionOrderBatchConfig {

    @Bean
    public Job subscriptionOrderJob(JobRepository jobRepository,
                                    Step orderStep) {
        return new JobBuilder("subscriptionOrderJob", jobRepository)
                .start(orderStep)
                .build();
    }

    @Bean
    public Step orderStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          SubscriptionRepository subscriptionRepository,
                          SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository){
        return new StepBuilder("orderStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate today = LocalDate.now();
                    List<Subscription> subscriptions = subscriptionRepository
                            .findAllByNextRunDateAndSubscriptionStatus(today, SubscriptionStatus.ACTIVE);

                    if (subscriptions.isEmpty()) {
                        log.info("No subscriptions to process for today");
                        return RepeatStatus.FINISHED;
                    }

                    subscriptions.forEach(subscription -> {
                        try {
                            requestOrder(subscription);

                            List<SubscriptionRecurrenceRule> rules =
                                    subscriptionRecurrenceRuleRepository.findBySubscriptionId(subscription.getId());
                            subscription.calculateNextRunDate(rules);
                            subscriptionRepository.save(subscription);

                            log.info("Order processed for subscription {}", subscription.getId());
                        } catch (Exception ex) {
                            log.error("Failed to process subscription {}: {}", subscription.getId(), ex.getMessage(), ex);
                        }
                    });
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private void requestOrder(Subscription subscription) {
        // TODO: 주문 도메인 API 연동
        log.info("Requesting order creation for subscription {}", subscription.getId());
    }
}
