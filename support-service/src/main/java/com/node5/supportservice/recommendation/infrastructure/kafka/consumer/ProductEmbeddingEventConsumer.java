package com.node5.supportservice.recommendation.infrastructure.kafka.consumer;

import com.node5.common.event.ProductEmbeddingEvent;
import com.node5.supportservice.recommendation.application.ProductEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEmbeddingEventConsumer {

    private final ProductEmbeddingService productEmbeddingService;

    @KafkaListener(topics = "${kafka.topics.product-embedding:ai-service.product-embedding.v1}")
    public void consume(ProductEmbeddingEvent event, Acknowledgment ack) {
        productEmbeddingService.handleProductEmbeddingEvent(event);
        ack.acknowledge();
    }
}
