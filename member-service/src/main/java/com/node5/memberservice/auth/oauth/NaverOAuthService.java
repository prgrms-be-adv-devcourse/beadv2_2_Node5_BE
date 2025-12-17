package com.node5.memberservice.auth.oauth;

import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.auth.oauth.dto.NaverTokenResponse;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverOAuthService implements OAuthProviderService {

    private static final String NAVER_TOKEN_REQUEST_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String NAVER_USER_REQUEST_URL = "https://openapi.naver.com/v1/nid/me";

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Override
    public String getProviderName() {
        return "naver";
    }

    private final RestTemplate restTemplate;

    @Override
    public OAuthUserInfo getUserInfo(String providerCode) {
        String accessToken = getAccessToken(providerCode);
        return requestUserInfo(accessToken);
    }

    public String getAccessToken(String providerCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", providerCode);
        body.add("state", "naver");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            NaverTokenResponse response = restTemplate.postForObject(NAVER_TOKEN_REQUEST_URL, entity, NaverTokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new AuthException(AuthErrorCode.OAUTH_RESPONSE_INVALID);
            }
            return response.accessToken();
        } catch (HttpStatusCodeException ex) {
            throw new AuthException(AuthErrorCode.OAUTH_TOKEN_ERROR);
        }
    }

    public OAuthUserInfo requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverUserResponse> result = restTemplate.exchange(NAVER_USER_REQUEST_URL, HttpMethod.GET, entity, NaverUserResponse.class);
            NaverUserResponse body = result.getBody();
            if (body == null || body.response() == null || body.response().id() == null) {
                throw new AuthException(AuthErrorCode.OAUTH_RESPONSE_INVALID);
            }

            return new OAuthUserInfo(getProviderName(), body.response().id());
        } catch (HttpStatusCodeException ex) {
            throw new AuthException(AuthErrorCode.OAUTH_USERINFO_ERROR);
        }
    }

    private record NaverUserResponse(Response response) {
        private record Response(String id) {
        }
    }
}
