package com.node5.memberservice.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
        @JsonProperty("id_token")
        String idToken
) {
}
