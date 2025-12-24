package com.node5.memberservice.endpoint.domain;

import com.node5.memberservice.member.domain.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EndPointRepository {
    List<Endpoint> findAllowedEndpoints(Set<MemberRole> roles, String httpMethod);
    Page<Endpoint> findAll(Pageable pageable);
    Endpoint save(Endpoint endpoint);
    List<Endpoint> findAll();

    void deleteById(UUID endPointId);

    Optional<Endpoint> findById(UUID endPointId);
}
