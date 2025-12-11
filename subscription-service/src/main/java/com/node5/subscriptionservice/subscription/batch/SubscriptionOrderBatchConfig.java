package com.node5.subscriptionservice.subscription.batch;

import com.node5.common.domain.ApiResponseDto;
import com.node5.subscriptionservice.subscription.client.OrderClient;
import com.node5.subscriptionservice.subscription.client.dto.OrderCreateInfo;
import com.node5.subscriptionservice.subscription.client.dto.OrderCreateRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                                    Step subscriptionOrderStep) {
        return new JobBuilder("subscriptionOrderJob", jobRepository)
                .start(subscriptionOrderStep)
                .build();
    }

    @Bean
    public Step subscriptionOrderStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager) {
        return new StepBuilder("subscriptionOrderStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String subscriptionParam = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("subscriptionId");
                    String runDateParam = (String) chunkContext.getStepContext()
                            .getJobParameters()
                            .get("runDate");

                    UUID subscriptionId = UUID.fromString(subscriptionParam);
                    LocalDate runDate = LocalDate.parse(runDateParam);

                    Subscription subscription = subscriptionRepository.findById(subscriptionId)
                            .orElseThrow(() -> new RuntimeException("subscription not found"));

                    requestOrder(subscription);

                    List<SubscriptionRecurrenceRule> rules =
                            subscriptionRecurrenceRuleRepository.findBySubscriptionId(subscription.getId());
                    subscription.calculateNextRunDate(rules);
                    subscriptionRepository.save(subscription);

                    log.info("Order processed subscription {} for runDAte = {}", subscriptionId, runDate);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private void requestOrder(Subscription subscription) {
        OrderCreateRequest request = new OrderCreateRequest(
                subscription.getMemberId(),
                "SUBSCRIPTION",
                subscription.getId(),
                null, //주문자name
                subscription.getDeliveryAddress(),
                List.of(
                        new OrderCreateRequest.OrderItemRequest(
                                subscription.getProductId(),
                                "",
                                "",
                                subscription.getPricePerItem(),
                                subscription.getQuantity(),
                                subscription.getTotalPrice()
                        )
                )
        );

        try {
            ResponseEntity<OrderCreateInfo> response = orderClient.create(request);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Order service responded with status " + response.getStatusCode());
            }

            Optional<OrderCreateInfo> responseBody = Optional.ofNullable(response.getBody());
            log.info("Requesting order creation for subscription {} -> order response: {}",
                    subscription.getId(),
                    responseBody.map(OrderCreateInfo::orderId).orElse(null));
        } catch (Exception ex) {
            log.error("Failed to request order for subscription {}: {}", subscription.getId(), ex.getMessage(), ex);
            throw new RuntimeException("Order request failed", ex);
        }
    }
}