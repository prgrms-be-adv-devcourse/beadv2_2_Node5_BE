package com.node5.batchservice.subscription.infrastructure.kafka.producer;

import com.node5.batchservice.subscription.batch.dto.SubscriptionBatchResult;
import com.node5.common.event.SubscriptionOrderBatchChunkResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionOrderBatchResultProducer {

    private final KafkaTemplate<String, SubscriptionOrderBatchChunkResultEvent> kafkaTemplate;

    @Value("${kafka.topics.subscription-order-batch-result:batch-service.subscription-order-batch-result.v1}")
    private String topic;

    public void sendChunk(LocalDate runDate, List<SubscriptionBatchResult> results) {
        List<SubscriptionOrderBatchChunkResultEvent.SubscriptionOrderBatchResultItem> items = results.stream()
                .map(result -> new SubscriptionOrderBatchChunkResultEvent.SubscriptionOrderBatchResultItem(
                        result.subscriptionId().toString(),
                        result.resultType(),
                        result.orderId() != null ? result.orderId().toString() : null
                ))
                .toList();
        SubscriptionOrderBatchChunkResultEvent event = new SubscriptionOrderBatchChunkResultEvent(
                runDate.toString(),
                items
        );
        kafkaTemplate.send(topic, runDate.toString(), event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send subscription batch chunk for {}: {}", runDate, ex.getMessage(), ex);
            }
        });
    }
}
