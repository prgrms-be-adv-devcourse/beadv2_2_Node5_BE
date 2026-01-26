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
import com.node5.catalogservice.inventory.application.dto.StockReleaseBatchCommand;
import com.node5.catalogservice.inventory.domain.Stock;
import com.node5.catalogservice.inventory.infrastructure.StockJpaRepository;
import com.node5.common.exception.BaseException;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@SpringBootTest
@ActiveProfiles("test")
public class InventoryReleaseConcurrencyTest {

	@Autowired
	InventoryReservationService reservationService;

	@Autowired
	StockJpaRepository stockJpaRepository;

	@Test
	void 같은_orderId로_release를_100번_동시에_요청해도_재고는_1번만_복구된다() throws Exception {
		//given
		UUID productId = UUID.randomUUID();
		stockJpaRepository.save(Stock.create(productId, 50));

		UUID orderId = UUID.randomUUID();
		reservationService.holdBatch(
			new StockHoldBatchCommand(
				orderId, List.of(new StockHoldBatchCommand.Item(productId, 1))
			)
		);

		assertEquals(49, stockJpaRepository.findById(productId).orElseThrow().getQuantity());

		int threads = 100;
		AtomicInteger success = new AtomicInteger();
		AtomicInteger fail = new AtomicInteger();

		// when
		동시실행(threads, () -> {
			try {
				reservationService.releaseBatch(
					new StockReleaseBatchCommand(
						orderId, List.of(new StockReleaseBatchCommand.Item(productId))
					)
				);
				success.incrementAndGet();
			} catch (BaseException e) {
				fail.incrementAndGet();
			}
		});

		// then
		int finalQty = stockJpaRepository.findById(productId).orElseThrow().getQuantity();

		assertEquals(50, finalQty);
		assertEquals(threads, success.get());
		assertEquals(0, fail.get());
	}

	@Test
	void hold와_release가_동시에_섞여_발생해도_재고정합성이_유지된다() throws Exception {
		// given
		UUID productId = UUID.randomUUID();
		stockJpaRepository.save(Stock.create(productId, 50));

		int n = 50;
		UUID[] preHeldOrders = new UUID[n];
		for (int i = 0; i < n; i++) {
			UUID orderId = UUID.randomUUID();
			preHeldOrders[i] = orderId;
			reservationService.holdBatch(
				new StockHoldBatchCommand(
					orderId, List.of(new StockHoldBatchCommand.Item(productId, 1))
				)
			);
		}

		assertEquals(0, stockJpaRepository.findById(productId).orElseThrow().getQuantity());

		AtomicInteger holdSuccess = new AtomicInteger();
		AtomicInteger releaseSuccess = new AtomicInteger();
		AtomicInteger releaseFail = new AtomicInteger();

		// when
		동시실행(
			() -> {
				for (int i = 0; i < n; i++) {
					UUID orderId = preHeldOrders[i];
					try {
						reservationService.releaseBatch(
							new StockReleaseBatchCommand(
								orderId, List.of(new StockReleaseBatchCommand.Item(productId))
							)
						);
						releaseSuccess.incrementAndGet();
					} catch (BaseException e) {
						releaseFail.incrementAndGet();
					}
				}
			},
			() -> {
				for (int i = 0; i < n; i++) {
					UUID newOrderId = UUID.randomUUID();

					boolean done = false;
					int attempts = 0;

					while (!done && attempts++ < 2000) {
						try {
							reservationService.holdBatch(
								new StockHoldBatchCommand(
									newOrderId, List.of(new StockHoldBatchCommand.Item(productId, 1))
								)
							);
							holdSuccess.incrementAndGet();
							done = true;
						} catch (BaseException e) {
							try { Thread.sleep(2); } catch (InterruptedException ignored) {}
						}
					}

					assertTrue(done, "hold의 재시도 횟수가 초과되었습니다.");
				}
			}
		);

		// then
		int finalQty = stockJpaRepository.findById(productId).orElseThrow().getQuantity();

		assertEquals(0, finalQty);

		assertEquals(50, holdSuccess.get());
		assertTrue(releaseSuccess.get() >= 1, "realeas는 최소 1회 성공해야 한다.");
	}
}
