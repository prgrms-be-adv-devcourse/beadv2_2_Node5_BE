package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.domain.EndPointRepository;
import com.node5.memberservice.auth.domain.Endpoint;
import com.node5.memberservice.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class EndPointRepositoryAdaptor implements EndPointRepository {

    private final EndPointJpaRepository endPointJpaRepository;

    @Override
    public List<Endpoint> findAllowedEndpoints(Set<MemberRole> roles, String method) {
        return endPointJpaRepository.findAllowedEndpoints(roles, method);
    }

    @Override
    public Endpoint save(Endpoint endpoint) {
        return endPointJpaRepository.save(endpoint);
    }

    @Override
    public List<Endpoint> findAll() {
        return endPointJpaRepository.findAll();
    }

}
