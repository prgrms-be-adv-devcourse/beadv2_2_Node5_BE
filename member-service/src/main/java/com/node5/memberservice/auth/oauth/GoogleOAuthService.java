package com.node5.memberservice.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.auth.oauth.dto.GoogleTokenResponse;
import com.node5.memberservice.auth.oauth.dto.KakaoTokenResponse;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService implements OAuthProviderService {

    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public OAuthUserInfo getUserInfo(OAuthLoginCommand command) {
        String idToken = getAccessToken(command.providerCode(), command.redirectUrl());
        return parseIdToken(idToken);
    }

    private String getAccessToken(String providerCode, String redirectUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", providerCode);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUrl);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            GoogleTokenResponse response = restTemplate.postForObject(GOOGLE_TOKEN_REQUEST_URL, entity, GoogleTokenResponse.class);
            if (response == null || response.idToken() == null) {
                throw new AuthException(AuthErrorCode.OAUTH_RESPONSE_INVALID);
            }
            return response.idToken();
        } catch (HttpStatusCodeException ex) {
            throw new AuthException(AuthErrorCode.OAUTH_TOKEN_ERROR);
        }
    }

    private OAuthUserInfo parseIdToken(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            Map<String, String> payloadMap = mapper.readValue(payload, Map.class);
            String providerId = payloadMap.get("sub");

            return new OAuthUserInfo(getProviderName(), providerId);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.OAUTH_USERINFO_ERROR);
        }
    }
}
