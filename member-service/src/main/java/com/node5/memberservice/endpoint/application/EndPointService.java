package com.node5.memberservice.endpoint.application;

import com.node5.memberservice.endpoint.application.dto.EndpointKey;
import com.node5.memberservice.endpoint.application.dto.EndPointCommand;
import com.node5.memberservice.endpoint.application.dto.EndPointInfoResponse;
import com.node5.memberservice.endpoint.domain.EndPointRepository;
import com.node5.memberservice.endpoint.domain.Endpoint;
import com.node5.memberservice.endpoint.exception.EndPointErrorCode;
import com.node5.memberservice.endpoint.exception.EndPointException;
import com.node5.memberservice.member.domain.MemberRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndPointService {

    private volatile Map<EndpointKey, List<Endpoint>> allowedEndpointCache = Map.of();

    private final EndPointRepository endPointRepository;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    public void refreshCache() {
        this.allowedEndpointCache = loadAllowedEndpoint();
    }

    private Map<EndpointKey, List<Endpoint>> loadAllowedEndpoint() {
        Map<EndpointKey, List<Endpoint>> newCache = new HashMap<>();
        List<Endpoint> endpoints = endPointRepository.findAll();
        for (Endpoint endpoint : endpoints) {
            EndpointKey key = new EndpointKey(endpoint.getRole(), endpoint.getHttpMethod());
            newCache.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(endpoint);
        }

        return newCache.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())
                ));
    }

    public Page<EndPointInfoResponse> getEndPointList(Pageable pageable) {
        Page<Endpoint> endpoints = endPointRepository.findAll(pageable);
        return endpoints.map(EndPointInfoResponse::from);
    }

    @Transactional
    public void createEndPoint(EndPointCommand command) {
        Endpoint endpoint = Endpoint.create(command);
        endPointRepository.save(endpoint);
        refreshCache();
    }

    @Transactional
    public void modifyEndPoint(UUID endPointId, EndPointCommand command) {
        Endpoint endPoint = endPointRepository.findById(endPointId)
                .orElseThrow(() -> new EndPointException(EndPointErrorCode.ENDPOINT_NOT_FOUND));
        endPoint.modify(command);
        refreshCache();
    }

    @Transactional
    public void deleteEndPoint(UUID endPointId) {
        endPointRepository.deleteById(endPointId);
        refreshCache();
    }

    public List<Endpoint> findAllowedEndpoints(Set<MemberRole> roles, String httpMethod) {

        Map<EndpointKey, List<Endpoint>> currentCache = allowedEndpointCache;
        List<Endpoint> allowedEndpoints = roles.stream()
                .flatMap(role -> currentCache.getOrDefault(
                        new EndpointKey(role, httpMethod),
                        List.of()
                ).stream())
                .distinct()
                .toList();

        if (allowedEndpoints.isEmpty()) {
            allowedEndpoints = endPointRepository.findAllowedEndpoints(roles, httpMethod);
        }

        return allowedEndpoints;
    }
}
