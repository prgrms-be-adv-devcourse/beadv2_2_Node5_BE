package com.node5.catalogservice.inventory.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.node5.catalogservice.inventory.domain.ProcessedEvent;
import com.node5.catalogservice.inventory.domain.ProcessedEventId;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {

	@Modifying
	@Query(value = """
        INSERT INTO catalog.processed_event (event_type, event_id, created_at)
        VALUES (:eventType, :eventId, now())
        ON CONFLICT (event_type, event_id) DO NOTHING
        """, nativeQuery = true)
	int tryInsert(
		@Param("eventType") String eventType,
		@Param("eventId") UUID eventId
	);
}
