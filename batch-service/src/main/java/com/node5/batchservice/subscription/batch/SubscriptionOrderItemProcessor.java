package com.node5.batchservice.subscription.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import com.node5.batchservice.subscription.batch.dto.SubscriptionBatchResult;
import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.dto.OrderCreateInfo;
import com.node5.batchservice.subscription.client.dto.OrderCreateRequest;
import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import com.node5.common.event.SubscriptionOrderBatchResultType;
import com.node5.common.exception.ExceptionResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                                target.thumbnailKey(),
                                target.pricePerItem(),
                                target.quantity(),
                                target.totalPrice()
                        )
                )
        );

        try {
            ResponseEntity<OrderCreateInfo> response = orderClient.create(target.memberId(), request);
            if (!response.getStatusCode().is2xxSuccessful()) {
                SubscriptionOrderBatchResultType resultType =
                        resolveResultType(null, response.getStatusCode().value());
                return failure(target, resultType);
            }

            Optional<OrderCreateInfo> responseBody = Optional.ofNullable(response.getBody());
            UUID orderId = responseBody.map(OrderCreateInfo::orderId).orElse(null);
            if (orderId == null) {
                return failure(target, SubscriptionOrderBatchResultType.RETRYABLE_FAILURE);
            }

            return new SubscriptionBatchResult(target.subscriptionId(), SubscriptionOrderBatchResultType.SUCCESS, orderId);
        } catch (FeignException ex) {
            String errorCode = extractErrorCode(ex);
            SubscriptionOrderBatchResultType resultType = resolveResultType(errorCode, ex.status());
            log.error("Failed to request order for subscription {}: {}", target.subscriptionId(), ex.getMessage(), ex);
            return failure(target, resultType);
        } catch (Exception ex) {
            log.error("Failed to request order for subscription {}: {}", target.subscriptionId(), ex.getMessage(), ex);
            return failure(target, SubscriptionOrderBatchResultType.RETRYABLE_FAILURE);
        }
    }

    private SubscriptionBatchResult failure(SubscriptionBatchTarget target, SubscriptionOrderBatchResultType resultType) {
        return new SubscriptionBatchResult(target.subscriptionId(), resultType, null);
    }

    private boolean isRetryableStatus(int statusCode) {
        return statusCode == 429 || (statusCode >= 500 && statusCode < 600);
    }

    private SubscriptionOrderBatchResultType resolveResultType(String errorCode, int statusCode) {
        if ("ORDER_005".equals(errorCode)) {
            return SubscriptionOrderBatchResultType.PAYMENT_FAILED;
        }
        if ("ORDER_008".equals(errorCode)) {
            return SubscriptionOrderBatchResultType.UNAVAILABLE;
        }
        return isRetryableStatus(statusCode)
                ? SubscriptionOrderBatchResultType.RETRYABLE_FAILURE
                : SubscriptionOrderBatchResultType.NON_RETRYABLE_FAILURE;
    }

    private String extractErrorCode(FeignException ex) {
        try {
            ExceptionResponseDto response = objectMapper.readValue(ex.contentUTF8(), ExceptionResponseDto.class);
            return response.code();
        } catch (Exception ignored) {
            return null;
        }
    }
}
