package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.EndPointCommand;
import com.node5.memberservice.auth.application.dto.EndPointInfoResponse;
import com.node5.memberservice.auth.domain.EndPointRepository;
import com.node5.memberservice.auth.domain.Endpoint;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndPointService {

    private final EndPointRepository endPointRepository;

    public Page<EndPointInfoResponse> getEndPointList(Pageable pageable) {
        Page<Endpoint> endpoints = endPointRepository.findAll(pageable);
        return endpoints.map(EndPointInfoResponse::from);
    }

    @Transactional
    public void createEndPoint(EndPointCommand command) {
        Endpoint endpoint = Endpoint.create(command);
        endPointRepository.save(endpoint);
    }

    @Transactional
    public void modifyEndPoint(UUID endPointId, EndPointCommand command) {
        Endpoint endPoint = endPointRepository.findById(endPointId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ENDPOINT_NOT_FOUND));

        endPoint.modify(command);
    }

    @Transactional
    public void deleteEndPoint(UUID endPointId) {
        endPointRepository.deleteById(endPointId);
    }
}
