package com.node5.memberservice.endpoint.application.dto;

import com.node5.memberservice.endpoint.domain.Endpoint;

import java.util.UUID;

public record EndPointInfoResponse(
        UUID id,
        String role,
        String httpMethod,
        String pathPattern
) {
    public static EndPointInfoResponse from(Endpoint endpoint) {
        return new EndPointInfoResponse(endpoint.getId(), endpoint.getRole().name(), endpoint.getHttpMethod(), endpoint.getPathPattern());
    }
}
