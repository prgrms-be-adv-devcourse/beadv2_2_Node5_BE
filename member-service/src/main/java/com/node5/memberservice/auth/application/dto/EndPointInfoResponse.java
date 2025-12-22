package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.auth.domain.Endpoint;

import java.util.UUID;

public record EndPointInfoResponse(
        UUID id,
        String role,
        String method,
        String pathPattern
) {
    public static EndPointInfoResponse from(Endpoint endpoint) {
        return new EndPointInfoResponse(endpoint.getId(), endpoint.getRole().name(), endpoint.getHttpMethod(), endpoint.getPathPattern());
    }
}
