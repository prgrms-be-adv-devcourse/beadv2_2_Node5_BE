package com.node5.orderservice.infrastructure;

import com.node5.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT nextval('order_num_seq')", nativeQuery = true)
    Long getNextSequenceNum();

}
