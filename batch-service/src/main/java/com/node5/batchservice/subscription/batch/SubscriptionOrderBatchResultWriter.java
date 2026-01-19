package com.node5.batchservice.subscription.batch;

import com.node5.batchservice.subscription.batch.dto.SubscriptionBatchResult;
import com.node5.batchservice.subscription.infrastructure.kafka.producer.SubscriptionOrderBatchResultProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class SubscriptionOrderBatchResultWriter implements ItemWriter<SubscriptionBatchResult> {

    private final SubscriptionOrderBatchResultProducer resultProducer;
    private final LocalDate runDate;

    @Override
    public void write(Chunk<? extends SubscriptionBatchResult> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        resultProducer.sendChunk(runDate, List.copyOf(items.getItems()));
    }
}
