package com.node5.catalogservice.inventory.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.application.dto.StockCommitBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockCommitCommand;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchResult;
import com.node5.catalogservice.inventory.application.dto.StockHoldCommand;
import com.node5.catalogservice.inventory.application.dto.StockReleaseBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockReleaseCommand;
import com.node5.catalogservice.inventory.application.dto.StockReservationInfo;
import com.node5.catalogservice.inventory.domain.ReservationStatus;
import com.node5.catalogservice.inventory.domain.StockRepository;
import com.node5.catalogservice.inventory.domain.StockReservation;
import com.node5.catalogservice.inventory.domain.StockReservationRepository;
import com.node5.catalogservice.inventory.exception.InventoryErrorCode;
import com.node5.common.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryReservationService {

	private final StockRepository stockRepository;
	private final StockReservationRepository reservationRepository;

	@Transactional
	public StockHoldBatchResult holdBatch(StockHoldBatchCommand command) {

		if (command == null || command.orderId() == null || command.items() == null || command.items().isEmpty()) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		Map<UUID, Integer> merged = new LinkedHashMap<>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null || item.quantity() <= 0) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			merged.merge(item.productId(), item.quantity(), Integer::sum);
		}

		var created = new ArrayList<StockReservationInfo>(merged.size());

		try {
			for (var entry : merged.entrySet()) {
				var info = holdInternal(new StockHoldCommand(command.orderId(), entry.getKey(), entry.getValue()));
				created.add(info);
			}
			return StockHoldBatchResult.of(command.orderId(), created);

		} catch (BaseException e) {
			for (var info : created) {
				try {
					releaseInternal(new StockReleaseCommand(command.orderId(), info.productId()));
				} catch (Exception ignore) {
				}
			}
			throw e;
		}
	}

	@Transactional
	public void commitBatch(StockCommitBatchCommand command) {

		if (command == null || command.orderId() == null || command.items() == null || command.items().isEmpty()) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		var unique = new LinkedHashSet<UUID>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			unique.add(item.productId());
		}

		for (UUID productId : unique) {
			commitInternal(new StockCommitCommand(command.orderId(), productId));
		}
	}

	@Transactional
	public void releaseBatch(StockReleaseBatchCommand command) {

		if (command == null || command.orderId() == null || command.items() == null || command.items().isEmpty()) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		var unique = new LinkedHashSet<UUID>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			unique.add(item.productId());
		}

		for (UUID productId : unique) {
			releaseInternal(new StockReleaseCommand(command.orderId(), productId));
		}
	}

	private StockReservationInfo holdInternal(StockHoldCommand command) {
		if (command == null || command.orderId() == null || command.productId() == null || command.quantity() <= 0) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		var existingOpt = reservationRepository.findByOrderIdAndProductId(command.orderId(), command.productId());
		if (existingOpt.isPresent()) {
			StockReservation existing = existingOpt.get();

			if (existing.getStatus() == ReservationStatus.HELD) {
				return StockReservationInfo.from(existing); // 멱등 성공
			}
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_PROCESSED);
		}

		boolean decreased = stockRepository.decreaseIfEnough(command.productId(), command.quantity());
		if (!decreased) {
			if (!stockRepository.existsById(command.productId())) {
				throw new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND);
			}
			throw new BaseException(InventoryErrorCode.OUT_OF_STOCK); // 409로 매핑
		}

		StockReservation reservation = StockReservation.held(
			command.orderId(), command.productId(), command.quantity()
		);

		StockReservation saved = reservationRepository.save(reservation);
		return StockReservationInfo.from(saved);
	}

	private void commitInternal(StockCommitCommand command) {
		if (command == null || command.orderId() == null || command.productId() == null) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		StockReservation reservation = reservationRepository
			.findByOrderIdAndProductId(command.orderId(), command.productId())
			.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

		if (reservation.getStatus() == ReservationStatus.COMMITTED) {
			return;
		}

		if (reservation.getStatus() == ReservationStatus.RELEASED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_RELEASED);
		}

		int updated = reservationRepository.updateStatus(
			command.orderId(),
			command.productId(),
			ReservationStatus.HELD,
			ReservationStatus.COMMITTED
		);

		if (updated == 0) {
			StockReservation latest = reservationRepository
				.findByOrderIdAndProductId(command.orderId(), command.productId())
				.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

			if (latest.getStatus() == ReservationStatus.COMMITTED) {
				return;
			}
			if (latest.getStatus() == ReservationStatus.RELEASED) {
				throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_RELEASED);
			}

			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}
	}

	private void releaseInternal(StockReleaseCommand command) {
		if (command == null || command.orderId() == null || command.productId() == null) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		StockReservation reservation = reservationRepository
			.findByOrderIdAndProductId(command.orderId(), command.productId())
			.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

		if (reservation.getStatus() == ReservationStatus.RELEASED) {
			return;
		}

		if (reservation.getStatus() == ReservationStatus.COMMITTED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_COMMITTED);
		}

		int updated = reservationRepository.updateStatus(
			command.orderId(),
			command.productId(),
			ReservationStatus.HELD,
			ReservationStatus.RELEASED
		);

		if (updated == 1) {
			int restored = stockRepository.increase(reservation.getProductId(), reservation.getQuantity());
			if (restored == 0) {
				throw new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND);
			}
			return;
		}

		StockReservation latest = reservationRepository
			.findByOrderIdAndProductId(command.orderId(), command.productId())
			.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

		if (latest.getStatus() == ReservationStatus.RELEASED) {
			return;
		}
		if (latest.getStatus() == ReservationStatus.COMMITTED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_COMMITTED);
		}

		throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
	}
}
