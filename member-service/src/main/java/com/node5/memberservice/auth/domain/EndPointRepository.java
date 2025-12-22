package com.node5.memberservice.auth.domain;

import java.util.List;
import java.util.UUID;

public interface EndPointRepository {
    List<Endpoint> findAllowedEndpoints(UUID memberId, String method);
    Endpoint save(Endpoint endpoint);
}
