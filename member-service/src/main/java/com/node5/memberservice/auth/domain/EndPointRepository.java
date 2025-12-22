package com.node5.memberservice.auth.domain;

import com.node5.memberservice.auth.application.dto.AuthorizeCommand;

public interface EndPointRepository {
    boolean authorize(AuthorizeCommand command);
    Endpoint save(Endpoint endpoint);
}
