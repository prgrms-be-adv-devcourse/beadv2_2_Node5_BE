package com.node5.memberservice.endpoint.domain;

import com.node5.memberservice.endpoint.application.dto.EndPointCommand;
import com.node5.memberservice.member.domain.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "endpoint", schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endpoint {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberRole role;

    @Column(name = "http_method", length = 10, nullable = false)
    private String httpMethod;

    @Column(name = "path_pattern", length = 200, nullable = false)
    private String pathPattern;

    private Endpoint(MemberRole role, String httpMethod, String pathPattern) {
        this.id = UUID.randomUUID();
        this.role = role;
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }

    public Endpoint(String role, String httpMethod, String pathPattern) {
        this.id = UUID.randomUUID();
        this.role = MemberRole.valueOf(role);
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }

    public static Endpoint create(EndPointCommand command) {
        return new Endpoint(
                command.role(),
                command.httpMethod(),
                command.pathPattern()
        );
    }

    public void modify(EndPointCommand command) {
        this.role = command.role();
        this.httpMethod = command.httpMethod();
        this.pathPattern = command.pathPattern();
    }
}
