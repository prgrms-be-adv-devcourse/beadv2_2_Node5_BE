package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.endpoint.domain.EndPointRepository;
import com.node5.memberservice.endpoint.domain.Endpoint;
import com.node5.memberservice.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EndPointRepositoryAdaptor implements EndPointRepository {

    private final EndPointJpaRepository endPointJpaRepository;

    @Override
    public List<Endpoint> findAllowedEndpoints(Set<MemberRole> roles, String method) {
        return endPointJpaRepository.findAllowedEndpoints(roles, method);
    }

    @Override
    public Page<Endpoint> findAll(Pageable pageable) {
        return endPointJpaRepository.findAll(pageable);
    }

    @Override
    public Endpoint save(Endpoint endpoint) {
        return endPointJpaRepository.save(endpoint);
    }

    @Override
    public List<Endpoint> findAll() {
        return endPointJpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID endPointId) {
        endPointJpaRepository.deleteById(endPointId);
    }

    @Override
    public Optional<Endpoint> findById(UUID endPointId) {
        return endPointJpaRepository.findById(endPointId);
    }

}
