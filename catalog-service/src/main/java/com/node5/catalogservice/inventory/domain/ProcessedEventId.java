package com.node5.catalogservice.inventory.domain;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ProcessedEventId implements Serializable {

	@Column(name = "event_type", length = 50, nullable = false)
	private String eventType;

	@Column(name = "event_id", nullable = false)
	private UUID eventId;

	public ProcessedEventId(String eventType, UUID eventId) {
		this.eventType = eventType;
		this.eventId = eventId;
	}
}
