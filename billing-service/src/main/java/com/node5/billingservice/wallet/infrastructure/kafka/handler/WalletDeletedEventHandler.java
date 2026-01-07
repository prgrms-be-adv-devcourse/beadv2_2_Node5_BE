package com.node5.billingservice.wallet.infrastructure.kafka.handler;

import com.node5.billingservice.wallet.infrastructure.kafka.producer.WalletDeletedProducer;
import com.node5.common.event.WalletDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WalletDeletedEventHandler {
    private final WalletDeletedProducer walletDeletedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WalletDeletedEvent event) {
        walletDeletedProducer.send(event);
    }
}
