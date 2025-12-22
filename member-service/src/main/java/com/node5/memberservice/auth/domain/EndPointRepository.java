package com.node5.memberservice.auth.domain;

import com.node5.memberservice.member.domain.MemberRole;

import java.util.List;
import java.util.Set;

public interface EndPointRepository {
    List<Endpoint> findAllowedEndpoints(Set<MemberRole> roles, String method);

    Endpoint save(Endpoint endpoint);
    List<Endpoint> findAll();
}
