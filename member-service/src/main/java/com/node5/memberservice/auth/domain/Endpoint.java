package com.node5.memberservice.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.endpoint.EndpointId;

import java.util.UUID;

@Entity
@Table(name = "endpoint", schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endpoint {
    @Id
    private UUID id;

    @Column(length = 20, nullable = false)
    private String role;

    @Column(name = "http_method", length = 10, nullable = false)
    private String httpMethod;

    @Column(name = "path_pattern", length = 200, nullable = false)
    private String pathPattern;

    protected Endpoint(String role, String httpMethod, String pathPattern) {
        this.id = UUID.randomUUID();
        this.role = role;
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }
}
