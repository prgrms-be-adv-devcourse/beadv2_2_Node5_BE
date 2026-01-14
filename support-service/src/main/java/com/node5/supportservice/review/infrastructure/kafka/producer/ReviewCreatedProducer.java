package com.node5.supportservice.review.infrastructure.kafka.producer;

import com.node5.common.event.MemberDeletedEvent;
import com.node5.common.event.ReviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCreatedProducer {

    private final KafkaTemplate<String, ReviewCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.review-created:support-service.review-created.v1}")
    private String topic;

    public void send(ReviewCreatedEvent event) {
        String key = event.reviewId().toString();
        // Kafka로 메시지 전송 로직 구현 (예: KafkaTemplate 사용)
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("리뷰 생성 토픽 발행 실패, key: {}, event: {}", key, event, ex);
            } else {
                log.info("리뷰 생성 토픽 발행 성공, key: {}, event: {}", key, event);
            }
        });

    }
}
