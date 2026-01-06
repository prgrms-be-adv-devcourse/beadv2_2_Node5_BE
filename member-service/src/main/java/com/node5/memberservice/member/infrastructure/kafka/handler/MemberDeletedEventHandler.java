package com.node5.memberservice.member.infrastructure.kafka.handler;

import com.node5.memberservice.member.application.event.MemberDeletedEvent;
import com.node5.memberservice.member.infrastructure.kafka.producer.MemberDeletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberDeletedEventHandler {
    private final MemberDeletedProducer memberDeletedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberDeletedEvent event) {
        memberDeletedProducer.send(event);
    }
}
