package com.node5.memberservice.auth.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "o_auth", schema = "public")
public class OAuth extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 100, nullable = false)
    private String providerId;

    private OAuth(Member member, String provider, String providerId) {
        this.id = UUID.randomUUID();
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static OAuth create(Member member, OAuthUserInfo oAuthUserInfo) {
        return new OAuth(member, oAuthUserInfo.provider(), oAuthUserInfo.providerId());
    }
}
