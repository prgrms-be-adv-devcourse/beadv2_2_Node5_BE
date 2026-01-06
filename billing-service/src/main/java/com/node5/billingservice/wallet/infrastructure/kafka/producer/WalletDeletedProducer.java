package com.node5.billingservice.wallet.infrastructure.kafka.producer;

import com.node5.common.event.WalletDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDeletedProducer {
    private final KafkaTemplate<String, WalletDeletedEvent> kafkaTemplate;

    @Value("${kafka.topics.wallet-deleted:billing-service.wallet-deleted.v1}")
    private String topic;

    public void send(WalletDeletedEvent walletDeletedEvent) {
        String key = walletDeletedEvent.walletId().toString();
        kafkaTemplate.send(topic, key, walletDeletedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("지갑 삭제 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
