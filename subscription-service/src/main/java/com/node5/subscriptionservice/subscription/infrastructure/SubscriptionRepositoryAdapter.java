package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.Subscription;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRepository;
import com.node5.subscriptionservice.subscription.domain.SubscriptionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;
    private final EntityManager entityManager;

    @Override
    public Optional<Subscription> findById(UUID id){return jpaRepository.findById(id);}

    @Override
    public Subscription save(Subscription subscription){return jpaRepository.save(subscription);}

    @Override
    public List<Subscription> findAllById(List<UUID> ids) {return jpaRepository.findAllById(ids);}

    @Override
    public List<Subscription> saveAll(List<Subscription> subscriptions){return jpaRepository.saveAll(subscriptions);}

    @Override
    public Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable){return jpaRepository.findAllByMemberId(memberId,pageable);}

    @Override
    public List<Subscription> findAllByMemberId(UUID memberId){return jpaRepository.findAllByMemberId(memberId);}

    @Override
    public Page<Subscription> findAllByNextRunDateAndSubscriptionStatus(LocalDate nextRunDate, SubscriptionStatus subscriptionStatus, Pageable pageable) {
        return jpaRepository.findAllByNextRunDateAndSubscriptionStatus(nextRunDate, subscriptionStatus, pageable);
    }

    @Override
    public Page<Subscription> findAllByProductId(UUID productId, Pageable pageable){
        return jpaRepository.findAllByProductId(productId, pageable);
    }

    @Override
    public void bulkUpdateNextRunDateByIds(List<UUID> ids, LocalDate nextRunDate) {
        jpaRepository.bulkUpdateNextRunDateByIds(ids, nextRunDate);
    }

    @Override
    public void bulkUpdateNextRunDates(Map<UUID, LocalDate> nextRunDates) {
        if (nextRunDates.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder(
                "update subscription.\"subscription\" set next_run_date = case id"
        );
        int paramIndex = 1;
        List<UUID> ids = new ArrayList<>(nextRunDates.size());

        for (Map.Entry<UUID, LocalDate> entry : nextRunDates.entrySet()) {
            sql.append(" when ?").append(paramIndex)
                    .append(" then cast(?").append(paramIndex + 1).append(" as date)");
            ids.add(entry.getKey());
            paramIndex += 2;
        }

        sql.append(" end where id in (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?").append(paramIndex + i);
        }
        sql.append(")");

        Query query = entityManager.createNativeQuery(sql.toString());
        paramIndex = 1;
        for (Map.Entry<UUID, LocalDate> entry : nextRunDates.entrySet()) {
            query.setParameter(paramIndex, entry.getKey());
            query.setParameter(paramIndex + 1, java.sql.Date.valueOf(entry.getValue()));
            paramIndex += 2;
        }
        for (UUID id : ids) {
            query.setParameter(paramIndex, id);
            paramIndex += 1;
        }
        query.executeUpdate();
    }

    @Override
    public void bulkMarkFailedByIds(List<UUID> ids) {
        jpaRepository.bulkMarkFailedByIds(ids);
    }

    @Override
    public void bulkTerminateAllByShop(UUID shopId, LocalDateTime dateTime){
        jpaRepository.bulkTerminateAllByShop(shopId, dateTime);
    }
}
