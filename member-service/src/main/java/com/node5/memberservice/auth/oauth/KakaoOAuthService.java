package com.node5.memberservice.auth.oauth;

import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
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

@Service
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthProviderService {

    private final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    @Value("${kakao.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Override
    public String getProviderName() {
        return "kakao";
    }

    @Override
    public OAuthUserInfo getUserInfo(String providerCode) {
        String accessToken = getAccessToken(providerCode);
        return requestUserInfo(accessToken);
    }

    private String getAccessToken(String providerCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", apiKey);
        body.add("redirect_uri", KAKAO_REDIRECT_URL);
        body.add("code", providerCode);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            KakaoTokenResponse response = restTemplate.postForObject(KAKAO_TOKEN_REQUEST_URL, entity, KakaoTokenResponse.class);
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
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            KakaoUserResponse result = restTemplate.postForObject(KAKAO_USER_REQUEST_URL, entity, KakaoUserResponse.class);
            if (result == null || result.id() == null) {
                throw new AuthException(AuthErrorCode.OAUTH_RESPONSE_INVALID);
            }

            return new OAuthUserInfo(getProviderName(), result.id().toString());
        } catch (HttpStatusCodeException ex) {
            throw new AuthException(AuthErrorCode.OAUTH_USERINFO_ERROR);
        }
    }

    private record KakaoUserResponse(
            Long id
    ) {
    }
}
