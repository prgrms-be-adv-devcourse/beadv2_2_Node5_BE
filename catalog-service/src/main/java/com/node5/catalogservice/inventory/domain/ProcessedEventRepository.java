package com.node5.catalogservice.inventory.domain;

import java.util.UUID;

public interface ProcessedEventRepository {

	/**
	 * 멱등 처리를 위해 이벤트 처리 여부를 기록합니다.
	 *
	 * @return true면 최초 처리(삽입 성공), false면 이미 처리됨(중복)
	 */
	boolean tryInsert(ProcessedEventType eventType, UUID eventId);
}
