package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.application.dto.AuthorizeCommand;
import com.node5.memberservice.auth.domain.EndPointRepository;
import com.node5.memberservice.auth.domain.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EndPointRepositoryAdaptor implements EndPointRepository {

    private final EndPointJpaRepository endPointJpaRepository;

    @Override
    public boolean authorize(AuthorizeCommand command) {
        return endPointJpaRepository.authorize(command.memberId(), command.method(), command.path());
    }

    @Override
    public Endpoint save(Endpoint endpoint) {
        return endPointJpaRepository.save(endpoint);
    }

}
