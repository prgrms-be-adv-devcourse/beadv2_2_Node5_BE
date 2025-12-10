package com.node5.memberservice.member.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.memberservice.member.presentation.dto.RoleAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"member\"", schema = "public")
public class Member extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 20)
    @Convert(converter = MemberRoleSetConverter.class)
    private Set<MemberRole> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    private LocalDateTime deletedAt;

    private Member(UUID id, String email, String name, String phoneNumber, String address, MemberRole role, MemberStatus status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.roles.add(role);
        this.status = status;
        this.deletedAt = null;
    }

    public static Member create(String email, String name, String phoneNumber, String address) {
        UUID id = UUID.randomUUID();
        MemberRole role = MemberRole.USER;
        MemberStatus status = MemberStatus.ACTIVE;

        return new Member(id, email, name, phoneNumber, address, role, status);
    }

    public void registerRequiredInfo(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = MemberStatus.ACTIVE;
    }

    public void modifyRoles(String role, RoleAction action) {
        if (action == RoleAction.ADD) {
            this.roles.add(MemberRole.valueOf(role));
        } else if (action == RoleAction.REMOVE) {
            this.roles.remove(MemberRole.valueOf(role));
        }
    }
}
