package com.node5.memberservice.oauth.infrastructure;

import com.node5.memberservice.oauth.domain.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthJpaRepository extends JpaRepository<OAuth, UUID> {
    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);
}
