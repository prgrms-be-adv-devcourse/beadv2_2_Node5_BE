package com.node5.catalogservice.inventory.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.application.dto.StockHoldCommand;
import com.node5.catalogservice.inventory.application.dto.StockReservationInfo;
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
}
