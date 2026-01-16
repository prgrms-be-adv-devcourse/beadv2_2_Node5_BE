package com.node5.catalogservice.inventory.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.application.dto.StockCommitBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockCommitCommand;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchCommand;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchResult;
import com.node5.catalogservice.inventory.application.dto.StockHoldCommand;
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

		// 1) productId 중복 합산 (입력 순서 유지)
		Map<UUID, Integer> merged = new LinkedHashMap<>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null || item.quantity() <= 0) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			merged.merge(item.productId(), item.quantity(), Integer::sum);
		}

		// 2) HOLD 진행 (성공한 것들만 기록)
		var created = new ArrayList<StockReservationInfo>(merged.size());

		try {
			for (var entry : merged.entrySet()) {
				var info = holdInternal(new StockHoldCommand(command.orderId(), entry.getKey(), entry.getValue()));
				created.add(info);
			}
			return StockHoldBatchResult.of(command.orderId(), created);

		} catch (BaseException e) {
			// 3) 하나라도 실패하면 지금까지 성공한 것들 전부 RELEASE로 보상
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

		// productId 중복 제거 (입력 순서 유지)
		Map<UUID, Boolean> dedup = new LinkedHashMap<>();
		for (var item : command.items()) {
			if (item == null || item.productId() == null) {
				throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
			}
			dedup.put(item.productId(), Boolean.TRUE);
		}

		for (var productId : dedup.keySet()) {
			commitInternal(new StockCommitCommand(command.orderId(), productId));
		}
	}

	@Transactional
	public void release(StockReleaseCommand command) {
		releaseInternal(command);
	}

	private StockReservationInfo holdInternal(StockHoldCommand command) {
		if (command == null || command.orderId() == null || command.productId() == null || command.quantity() <= 0) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		// 1) 멱등: 기존 예약이 있으면 처리
		var existingOpt = reservationRepository.findByOrderIdAndProductId(command.orderId(), command.productId());
		if (existingOpt.isPresent()) {
			StockReservation existing = existingOpt.get();

			if (existing.getStatus() == ReservationStatus.HELD) {
				return StockReservationInfo.from(existing); // 멱등 성공
			}
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_PROCESSED);
		}

		// 2) 조건부 차감
		boolean decreased = stockRepository.decreaseIfEnough(command.productId(), command.quantity());
		if (!decreased) {
			if (!stockRepository.existsById(command.productId())) {
				throw new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND);
			}
			throw new BaseException(InventoryErrorCode.OUT_OF_STOCK); // 409로 매핑
		}

		// 3) 예약 생성(HELD)
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

		// 1) 멱등: 이미 COMMITTED면 성공 처리
		if (reservation.getStatus() == ReservationStatus.COMMITTED) {
			return;
		}

		// 2) 정책: RELEASED면 commit 불가
		if (reservation.getStatus() == ReservationStatus.RELEASED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_RELEASED);
		}

		// 3) HELD -> COMMITTED 조건부 전이
		int updated = reservationRepository.updateStatus(
			command.orderId(),
			command.productId(),
			ReservationStatus.HELD,
			ReservationStatus.COMMITTED
		);

		if (updated == 0) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_RELEASED);
		}
	}

	private void releaseInternal(StockReleaseCommand command) {
		if (command == null || command.orderId() == null || command.productId() == null) {
			throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
		}

		// 1) 예약 조회
		StockReservation reservation = reservationRepository
			.findByOrderIdAndProductId(command.orderId(), command.productId())
			.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

		// 2) 멱등: 이미 RELEASED면 성공 처리
		if (reservation.getStatus() == ReservationStatus.RELEASED) {
			return;
		}

		// 3) 정책: COMMITTED 상태는 해제 불가
		if (reservation.getStatus() == ReservationStatus.COMMITTED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_COMMITTED);
		}

		// 4) HELD -> RELEASED 조건부 전이
		int updated = reservationRepository.updateStatus(
			command.orderId(),
			command.productId(),
			ReservationStatus.HELD,
			ReservationStatus.RELEASED
		);

		// 5) 상태 전이에 성공한 경우에만 재고 복구
		if (updated == 1) {
			int restored = stockRepository.increase(reservation.getProductId(), reservation.getQuantity());
			if (restored == 0) {
				throw new BaseException(InventoryErrorCode.INVENTORY_NOT_FOUND);
			}
			return;
		}

		// 6) row=0이면 이미 다른 트랜잭션에서 상태가 바뀐 상태 → 최신 상태 확인
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
