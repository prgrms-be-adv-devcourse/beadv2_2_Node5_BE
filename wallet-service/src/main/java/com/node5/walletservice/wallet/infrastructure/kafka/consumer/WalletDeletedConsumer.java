package com.node5.walletservice.wallet.infrastructure.kafka.consumer;

import com.node5.walletservice.wallet.application.WalletService;
import com.node5.common.event.MemberDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDeletedConsumer {
    private final WalletService walletService;

    @KafkaListener(topics = "${kafka.topics.member-deleted:member-service.member-deleted.v1}")
    public void consume(MemberDeletedEvent event, Acknowledgment ack) {
        walletService.deleteWallet(event.memberId());
        ack.acknowledge();
    }
}
