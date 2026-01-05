package com.node5.memberservice.kafka.handler;

import com.node5.memberservice.kafka.dto.MemberDeletedEvent;
import com.node5.memberservice.kafka.producer.DeleteMemberProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberDeletedEventHandler {
    private final DeleteMemberProducer deleteMemberProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberDeletedEvent event) {
        deleteMemberProducer.send(event);
    }
}
