package com.node5.walletservice.wallet.infrastructure.kafka.consumer;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.walletservice.wallet.application.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDepositEventConsumer {
    private final WalletService walletService;

    @KafkaListener(topics = "${kafka.topics.deposit-event:payment-service.deposit-event.v1}")
    public void consume(PaymentDepositEvent event, Acknowledgment ack) {
        walletService.depositConfirm(event);
        ack.acknowledge();
    }
}