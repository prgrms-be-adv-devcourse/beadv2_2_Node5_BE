package com.node5.batchservice.subscription.batch;

import com.node5.batchservice.subscription.batch.dto.SubscriptionBatchResult;
import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.dto.OrderCreateInfo;
import com.node5.batchservice.subscription.client.dto.OrderCreateRequest;
import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionOrderItemProcessor implements ItemProcessor<SubscriptionBatchTarget, SubscriptionBatchResult> {

    private final OrderClient orderClient;
    private final LocalDate runDate;

    @Override
    public SubscriptionBatchResult process(SubscriptionBatchTarget target) {
        UUID subscriptionKey = UUID.nameUUIDFromBytes(
                (target.subscriptionId().toString() + ":" + runDate).getBytes(StandardCharsets.UTF_8)
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "SUBSCRIPTION",
                subscriptionKey,
                "subscription-batch",
                target.deliveryAddress(),
                List.of(
                        new OrderCreateRequest.OrderItemRequest(
                                target.productId(),
                                target.productName(),
                                target.thumbnailUrl(),
                                target.pricePerItem(),
                                target.quantity(),
                                target.totalPrice()
                        )
                )
        );

        try {
            ResponseEntity<OrderCreateInfo> response = orderClient.create(target.memberId(), request);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String reason = "order-service status " + response.getStatusCode();
                return failure(target, reason, isRetryableStatus(response.getStatusCode()));
            }

            Optional<OrderCreateInfo> responseBody = Optional.ofNullable(response.getBody());
            UUID orderId = responseBody.map(OrderCreateInfo::orderId).orElse(null);
            if (orderId == null) {
                return failure(target, "order-service empty body", true);
            }

            return new SubscriptionBatchResult(target.subscriptionId(), true, orderId, null, false);
        } catch (Exception ex) {
            log.error("Failed to request order for subscription {}: {}", target.subscriptionId(), ex.getMessage(), ex);
            return failure(target, ex.getMessage(), true);
        }
    }

    private SubscriptionBatchResult failure(SubscriptionBatchTarget target, String reason, boolean retryable) {
        return new SubscriptionBatchResult(target.subscriptionId(), false, null, reason, retryable);
    }

    private boolean isRetryableStatus(HttpStatusCode statusCode) {
        int code = statusCode.value();
        return code == 429 || (code >= 500 && code < 600);
    }
}
