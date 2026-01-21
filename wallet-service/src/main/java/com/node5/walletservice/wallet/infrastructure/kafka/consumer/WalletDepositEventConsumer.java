package com.node5.walletservice.wallet.infrastructure.kafka.consumer;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.walletservice.wallet.application.WalletService;
import com.node5.walletservice.wallet.exception.WalletException;
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
        log.info("Kafka 입금 이벤트 수신, orderId={}", event.orderId());
        try {
            walletService.depositRequest(event);
            ack.acknowledge();
        } catch (WalletException e) {
            log.error("비즈니스 로직 에러 - 재시도하지 않음. orderId: {}, 사유: {}", event.orderId(), e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("시스템 에러 발생 - 재시도 유도. orderId: {}, 사유: {}", event.orderId(), e.getMessage());
            throw e;
        }
    }
}