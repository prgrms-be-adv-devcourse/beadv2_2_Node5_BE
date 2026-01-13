package com.node5.catalogservice.inventory.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.application.dto.StockCommitCommand;
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
public class InventoryService {

	private final StockRepository stockRepository;
	private final StockReservationRepository reservationRepository;

	@Transactional
	public StockReservationInfo hold(StockHoldCommand command) {

		// 1) 멱등: 기존 예약이 있으면 처리
		var existingOpt = reservationRepository.findByOrderIdAndProductId(command.orderId(), command.productId());
		if (existingOpt.isPresent()) {
			StockReservation existing = existingOpt.get();

			if (existing.isHeld()) {
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
			throw new BaseException(InventoryErrorCode.OUT_OF_STOCK);
		}

		// 3) 예약 생성(HELD)
		StockReservation reservation = StockReservation.held(
			command.orderId(), command.productId(), command.quantity()
		);

		StockReservation saved = reservationRepository.save(reservation);
		return StockReservationInfo.from(saved);
	}


	@Transactional
	public void commit(StockCommitCommand command) {

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

		// 3) HELD -> COMMITTED 조건부 전이 (동시 commit/release 경쟁에서도 1번만 성공)
		int updated = reservationRepository.updateStatus(
			command.orderId(),
			command.productId(),
			ReservationStatus.HELD,
			ReservationStatus.COMMITTED
		);

		// 4) row=0이면 HELD가 아니라는 뜻(대부분 동시 release로 바뀐 케이스)
		if (updated == 0) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_RELEASED);
		}
	}

	@Transactional
	public void release(StockReleaseCommand command) {

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

		// 4) HELD -> RELEASED 조건부 전이 (동시 commit/release 경쟁에서도 1번만 성공)
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

		// 6) row=0이면 이미 다른 트랜잭션에서 상태가 바뀐 상태
		StockReservation latest = reservationRepository
			.findByOrderIdAndProductId(command.orderId(), command.productId())
			.orElseThrow(() -> new BaseException(InventoryErrorCode.RESERVATION_NOT_FOUND));

		// 7) 멱등: 이미 RELEASED면 성공
		if (latest.getStatus() == ReservationStatus.RELEASED) {
			return;
		}

		// 8) COMMITTED로 바뀐 경우 해제 불가
		if (latest.getStatus() == ReservationStatus.COMMITTED) {
			throw new BaseException(InventoryErrorCode.RESERVATION_ALREADY_COMMITTED);
		}

		// 9) 그 외는 비정상 케이스로 간주
		throw new BaseException(InventoryErrorCode.INVALID_REQUEST);
	}
}
