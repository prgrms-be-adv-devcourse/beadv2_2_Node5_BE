package com.node5.memberservice.member.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(length = 20)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

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
        this.role = role;
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
}
