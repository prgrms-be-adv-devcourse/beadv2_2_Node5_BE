package com.node5.subscriptionservice.subscription.batch;

import com.node5.subscriptionservice.subscription.client.OrderClient;
import com.node5.subscriptionservice.subscription.client.dto.OrderCreateInfo;
import com.node5.subscriptionservice.subscription.client.dto.OrderCreateRequest;
import com.node5.subscriptionservice.subscription.domain.*;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.node5.subscriptionservice.subscription.exception.SubscriptionErrorCode.*;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SubscriptionOrderBatchConfig {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;
    private final OrderClient orderClient;

    @Bean
    public Job subscriptionOrderJob(JobRepository jobRepository,
                                    Step subscriptionOrderRequestStep,
                                    Step updateNextRunDateStep) {
        return new JobBuilder("subscriptionOrderJob", jobRepository)
                .start(subscriptionOrderRequestStep)
                .next(updateNextRunDateStep)
                .build();
    }

    @Bean
    public Step subscriptionOrderRequestStep(JobRepository jobRepository,
                                             PlatformTransactionManager transactionManager) {
        return new StepBuilder("subscriptionOrderRequestStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String subscriptionParam = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("subscriptionId");

                    UUID subscriptionId = UUID.fromString(subscriptionParam);

                    Subscription subscription = subscriptionRepository.findById(subscriptionId)
                            .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

                    String runDateParam = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("runDate");
                    LocalDate runDate = LocalDate.parse(runDateParam);
                    requestOrder(subscription, runDate);

                    log.info("Order processed subscription {} for runDate = {}", subscriptionId, runDate);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step updateNextRunDateStep(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager) {
        return new StepBuilder("subscriptionNextRunDateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String subscriptionParam = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("subscriptionId");

                    UUID subscriptionId = UUID.fromString(subscriptionParam);

                    Subscription subscription = subscriptionRepository.findById(subscriptionId)
                            .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

                    List<SubscriptionRecurrenceRule> rules =
                            subscriptionRecurrenceRuleRepository.findAllBySubscriptionId(subscription.getId());
                    subscription.calculateNextRunDate(rules);
                    subscriptionRepository.save(subscription);

                    log.info("Next run date updated for subscription {}", subscriptionId);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private void requestOrder(Subscription subscription, LocalDate runDate) {
        UUID subscriptionKey = UUID.nameUUIDFromBytes(
                (subscription.getId().toString() + ":" + runDate).getBytes(StandardCharsets.UTF_8)
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "SUBSCRIPTION",
                subscriptionKey,
                "subscription-batch",
                subscription.getDeliveryAddress(),
                List.of(
                        new OrderCreateRequest.OrderItemRequest(
                                subscription.getProductId(),
                                subscription.getProductName(),
                                subscription.getThumbnailUrl(),
                                subscription.getPricePerItem(),
                                subscription.getQuantity(),
                                subscription.getTotalPrice()
                        )
                )
        );

        try {
            ResponseEntity<OrderCreateInfo> response = orderClient.create(subscription.getMemberId(), request);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info("Order for SUBSCRIPTION {} is not success with status {}", subscription.getMemberId(), response.getStatusCode());
                throw new SubscriptionException(SUBSCRIPTION_ORDER_REQUEST_FAILED);
            }

            Optional<OrderCreateInfo> responseBody = Optional.ofNullable(response.getBody());
            log.info("Requesting order creation for subscription {} -> order response: {}",
                    subscription.getId(),
                    responseBody.map(OrderCreateInfo::orderId).orElse(null));
        } catch (Exception ex) {
            log.error("Failed to request order for subscription {}: {}", subscription.getId(), ex.getMessage(), ex);
            throw new SubscriptionException(SUBSCRIPTION_ORDER_REQUEST_FAILED);
        }
    }
}
