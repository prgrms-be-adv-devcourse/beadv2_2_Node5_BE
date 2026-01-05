package com.node5.memberservice.kafka.producer;

import com.node5.memberservice.kafka.dto.MemberDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMemberProducer {

    private final KafkaTemplate<String, MemberDeletedEvent> kafkaTemplate;

    private final static String deleteMemberTopic = "delete-member";

    public void send(MemberDeletedEvent memberDeletedEvent) {
        kafkaTemplate.send(deleteMemberTopic, memberDeletedEvent);
    }
}
