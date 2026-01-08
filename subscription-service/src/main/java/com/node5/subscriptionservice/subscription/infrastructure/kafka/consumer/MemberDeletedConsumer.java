package com.node5.subscriptionservice.subscription.infrastructure.kafka.consumer;

import com.node5.common.event.MemberDeletedEvent;
import com.node5.subscriptionservice.subscription.application.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDeletedConsumer {

    private final SubscriptionService subscriptionService;

    @KafkaListener(topics = "${kafka.topics.member-deleted:member-service.member-deleted.v1}")
    public void consume(MemberDeletedEvent event, Acknowledgment ack) {
        subscriptionService.terminateUserSubscriptions(event.memberId());
        ack.acknowledge();
    }
}
