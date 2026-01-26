package com.node5.catalogservice.inventory.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "processed_event", schema = "catalog")
public class ProcessedEvent {

	@EmbeddedId
	private ProcessedEventId id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private ProcessedEvent(ProcessedEventId id) {
		this.id = id;
		this.createdAt = LocalDateTime.now();
	}

	public static ProcessedEvent of(ProcessedEventType type, UUID eventId) {
		return new ProcessedEvent(new ProcessedEventId(type.name(), eventId));
	}
}
