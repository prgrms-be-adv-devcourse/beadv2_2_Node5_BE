package com.node5.memberservice.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role", schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberRole name;

    protected Role(MemberRole role) {
        this.name = role;
    }
}
