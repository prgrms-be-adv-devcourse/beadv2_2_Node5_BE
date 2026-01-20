package com.node5.memberservice.shop.infrastructure.kafka.consumer;

import com.node5.common.event.MemberDeletedEvent;
import com.node5.memberservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDeletedConsumer {

    private final ShopService shopService;

    @KafkaListener(topics = "${kafka.topics.member-deleted:member-service.member-deleted.v1}")
    public void consume(MemberDeletedEvent event, Acknowledgment ack) {
        shopService.deleteAllMyShop(event.memberId());
        ack.acknowledge();
    }
}
