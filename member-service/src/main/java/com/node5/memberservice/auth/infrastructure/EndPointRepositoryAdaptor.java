package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.application.dto.AuthorizeCommand;
import com.node5.memberservice.auth.domain.EndPointRepository;
import com.node5.memberservice.auth.domain.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EndPointRepositoryAdaptor implements EndPointRepository {

    private final EndPointJpaRepository endPointJpaRepository;

    @Override
    public List<Endpoint> findAllowedEndpoints(UUID memberId, String method) {
        return endPointJpaRepository.findAllowedEndpoints(memberId, method);
    }

    @Override
    public Endpoint save(Endpoint endpoint) {
        return endPointJpaRepository.save(endpoint);
    }

}
