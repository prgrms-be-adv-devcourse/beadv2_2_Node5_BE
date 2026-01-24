package com.node5.catalogservice.inventory.infrastructure.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.node5.catalogservice.inventory.application.InventoryRestoreService;
import com.node5.catalogservice.inventory.application.dto.StockRestoreBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockRestoreItemCommand;
import com.node5.common.event.StockRestoreEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStockRestoreEventConsumer {

	private final InventoryRestoreService inventoryRestoreService;

	@KafkaListener(
		topics = "${app.kafka.topics.stock-restore}",
		groupId = "${spring.kafka.consumer.group-id:catalog-service-inventory}"
	)
	public void consume(StockRestoreEvent event) {

		if (event == null || event.cancelEventId() == null || event.orderId() == null || event.items() == null) {
			log.warn("재고 복원 이벤트가 null이거나 필수 값이 없습니다. event={}", event);
			return;
		}

		log.info("Kafka 재고 복원 이벤트 수신, orderId={}, cancelEventId={}, itemsCount={}",
			event.orderId(), event.cancelEventId(), event.items().size());

		try {
			StockRestoreBatchCommand command = toCommand(event);
			inventoryRestoreService.restoreBatch(command);

			log.info("재고 복원 처리 완료, orderId={}, cancelEventId={}", event.orderId(), event.cancelEventId());

		} catch (Exception e) {
			log.error("재고 복원 처리 실패, orderId={}, cancelEventId={}", event.orderId(), event.cancelEventId(), e);
			throw e;
		}
	}

	private StockRestoreBatchCommand toCommand(StockRestoreEvent event) {
		List<StockRestoreItemCommand> items = event.items().stream()
			.map(i -> new StockRestoreItemCommand(i.productId(), i.quantity()))
			.toList();

		return new StockRestoreBatchCommand(event.orderId(), event.cancelEventId(), items);
	}
}
