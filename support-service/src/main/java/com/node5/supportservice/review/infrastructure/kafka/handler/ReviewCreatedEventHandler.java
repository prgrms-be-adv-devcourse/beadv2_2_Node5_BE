package com.node5.supportservice.review.infrastructure.kafka.handler;

import com.node5.common.event.ReviewCreatedEvent;
import com.node5.supportservice.review.infrastructure.kafka.producer.ReviewCreatedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReviewCreatedEventHandler {
    private final ReviewCreatedProducer reviewCreatedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReviewCreatedEvent event) {
        reviewCreatedProducer.send(event);
    }
}
