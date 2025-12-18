package com.node5.memberservice.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverTokenResponse(
        @JsonProperty("access_token")
        String accessToken
) {
}
