package com.node5.paymentservice.payment.infrastructure;

import com.node5.paymentservice.payment.domain.PaymentOutbox;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // -2는 SKIP LOCKED를 의미 (PostgreSQL/Oracle/MySQL 8.0)
    @Query("""
        SELECT o FROM PaymentOutbox o
        WHERE o.status = 'READY'
        ORDER BY o.createdAt ASC
    """)
    List<PaymentOutbox> findReadyForUpdate(Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM PaymentOutbox p " +
            "WHERE p.status = 'SENT' " +
            "AND p.createdAt < :threshold")
    int deleteOldPublishedMessages(LocalDateTime threshold);

}
