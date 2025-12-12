package com.node5.memberservice.member.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.memberservice.auth.application.dto.OAuthRegisterCommand;
import com.node5.memberservice.member.application.dto.MemberModifyCommand;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"member\"", schema = "member")
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

    public static Member create(OAuthRegisterCommand command) {
        UUID id = UUID.randomUUID();
        MemberRole role = MemberRole.USER;
        MemberStatus status = MemberStatus.ACTIVE;

        return new Member(id, command.email(), command.name(), command.phoneNumber(), command.address(), role, status);
    }

    public void addRole(MemberRole role) {
        this.roles.add(role);
    }

    public void deleteRole(MemberRole role) {
        this.roles.remove(role);
    }

    public void modifyInfo(MemberModifyCommand command) {
        this.name = command.name();
        this.phoneNumber = command.phoneNumber();
        this.address = command.address();
    }

    public void delete() {
        this.status = MemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
