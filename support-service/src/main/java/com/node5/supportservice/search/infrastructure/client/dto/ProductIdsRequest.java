package com.node5.supportservice.search.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record ProductIdsRequest(List<UUID> productIds) {
}
