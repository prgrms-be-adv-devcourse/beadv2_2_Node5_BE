package com.node5.catalogservice.product.presentation.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public record ProductIdsRequest(
	@NotEmpty
	List<UUID> productIds
) {
}
