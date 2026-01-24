package com.node5.catalogservice.inventory.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.inventory.domain.ProcessedEventRepository;
import com.node5.catalogservice.inventory.domain.ProcessedEventType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

	private final ProcessedEventJpaRepository processedEventJpaRepository;

	@Override
	@Transactional
	public boolean tryInsert(ProcessedEventType type, UUID eventId) {
		return processedEventJpaRepository.tryInsert(type.name(), eventId) == 1;
	}
}
