package com.node5.memberservice.oauth.domain;

import java.util.Optional;

public interface OAuthRepository {

    OAuth save(OAuth oAuth);

    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);
}
