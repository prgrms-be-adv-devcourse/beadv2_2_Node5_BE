package com.node5.catalogservice.inventory.application;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.application.dto.StockRestoreBatchCommand;
import com.node5.catalogservice.inventory.domain.ProcessedEventRepository;
import com.node5.catalogservice.inventory.domain.ProcessedEventType;
import com.node5.catalogservice.inventory.domain.StockRepository;
import com.node5.catalogservice.inventory.exception.InventoryErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRestoreService {

	private final StockRepository stockRepository;
	private final ProcessedEventRepository processedEventRepository;

	@Transactional
	public void restoreBatch(StockRestoreBatchCommand command) {

		if (command == null || command.orderId() == null || command.cancelEventId() == null
			|| command.items() == null || command.items().isEmpty()) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		Map<UUID, Integer> merged = new LinkedHashMap<>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null || item.quantity() <= 0) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			merged.merge(item.productId(), item.quantity(), Integer::sum);
		}

		boolean firstTime = processedEventRepository.tryInsert(
			ProcessedEventType.STOCK_RESTORE,
			command.cancelEventId()
		);

		if (!firstTime) {
			return;
		}

		for (var entry : merged.entrySet()) {
			UUID productId = entry.getKey();
			int quantity = entry.getValue();

			int restored = stockRepository.increase(productId, quantity);
			if (restored == 0) {
				throw new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND);
			}
		}
	}
}
