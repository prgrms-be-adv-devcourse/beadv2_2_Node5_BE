package com.node5.memberservice.kafka.producer;

import com.node5.memberservice.kafka.dto.MemberDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDeletedProducer {

    private final KafkaTemplate<String, MemberDeletedEvent> kafkaTemplate;

    @Value("${kafka.topics.member-deleted:member-service.member-deleted.v1}")
    private String topic;

    public void send(MemberDeletedEvent memberDeletedEvent) {
        String key = memberDeletedEvent.memberId().toString();
        kafkaTemplate.send(topic, key, memberDeletedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("회원 탈퇴 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
