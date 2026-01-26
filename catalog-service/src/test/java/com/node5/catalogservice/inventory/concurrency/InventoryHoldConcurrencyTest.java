package com.node5.catalogservice.inventory.concurrency;

import static com.node5.catalogservice.testsupport.ConcurrentTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.node5.catalogservice.inventory.application.InventoryReservationService;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchCommand;
import com.node5.catalogservice.inventory.domain.Stock;
import com.node5.catalogservice.inventory.infrastructure.StockJpaRepository;
import com.node5.common.exception.BaseException;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@SpringBootTest
@ActiveProfiles("test")
public class InventoryHoldConcurrencyTest {

	@Autowired
	InventoryReservationService reservationService;

	@Autowired
	StockJpaRepository stockJpaRepository;

	@Test
	void 재고가_50일때_100명이_동시에_hold하면_성공50_실패50_최종0이다() throws Exception {
		// given
		UUID productId = UUID.randomUUID();
		stockJpaRepository.save(Stock.create(productId, 50));

		int threads = 100;
		AtomicInteger success = new AtomicInteger();
		AtomicInteger fail = new AtomicInteger();

		// when
		동시실행(threads, () -> {
			UUID orderId = UUID.randomUUID();
			try {
				reservationService.holdBatch(
					new StockHoldBatchCommand(
						orderId, List.of(new StockHoldBatchCommand.Item(productId, 1))
					)
				);
				success.incrementAndGet();
			} catch (BaseException e) {
				fail.incrementAndGet();
			}
		});

		// then
		int finalQty = stockJpaRepository.findById(productId).orElseThrow().getQuantity();
		assertEquals(50, success.get());
		assertEquals(50, fail.get());
		assertEquals(0, finalQty);
	}
}
