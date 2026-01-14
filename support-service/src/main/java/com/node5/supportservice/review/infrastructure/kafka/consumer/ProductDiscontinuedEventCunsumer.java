package com.node5.supportservice.review.infrastructure.kafka.consumer;

import com.node5.common.event.ProductDiscontinuedEvent;
import com.node5.common.event.ReviewCreatedEvent;
import com.node5.supportservice.review.application.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDiscontinuedEventCunsumer {
    ReviewService reviewService;

    @KafkaListener(topics = "${kafka.topics.product-discontinued:catalog-service.product-discontinued.v1}")
    public void consume(ProductDiscontinuedEvent event, Acknowledgment ack) {
        reviewService.deleteReviewStatic(event);
        ack.acknowledge();
    }
}
