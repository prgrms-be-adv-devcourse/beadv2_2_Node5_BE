package com.node5.memberservice.redis.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:";
    private static final String VERIFIED_EMAIL_KEY_PREFIX = "email:verified:";
    private static final String EMAIL_VERIFY_CODE_KEY_PREFIX = "email:verify:";
    private static final String OAUTH_TEMP_KEY_PREFIX = "oauth:temp:";

    public void saveRefreshToken(UUID memberId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_KEY_PREFIX + memberId;
        stringRedisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(UUID memberId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + memberId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(UUID memberId) {
        stringRedisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + memberId);
    }

    public void saveVerificationCode(String email, String code) {
        stringRedisTemplate.opsForValue().set(EMAIL_VERIFY_CODE_KEY_PREFIX + email, code, Duration.ofMinutes(10));
    }

    public String getVerificationCode(String email) {
        return stringRedisTemplate.opsForValue().get(EMAIL_VERIFY_CODE_KEY_PREFIX + email);
    }

    public void deleteVerificationCode(String email) {
        stringRedisTemplate.delete(EMAIL_VERIFY_CODE_KEY_PREFIX + email);
    }

    public void markVerifiedEmail(String email) {
        stringRedisTemplate.opsForValue().set(VERIFIED_EMAIL_KEY_PREFIX + email, "true", Duration.ofMinutes(10));
    }

    public String getVerifiedEmail(String email) {
        return stringRedisTemplate.opsForValue().get(VERIFIED_EMAIL_KEY_PREFIX + email);
    }

    public void deleteVerifiedEmail(String email) {
        stringRedisTemplate.delete(VERIFIED_EMAIL_KEY_PREFIX + email);
    }

    public void saveOAuthTempUser(UUID tempId, OAuthUserInfo oAuthUserInfo) {
        try {
            String key = OAUTH_TEMP_KEY_PREFIX + tempId;
            String value = objectMapper.writeValueAsString(oAuthUserInfo);
            stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            throw new AuthException(AuthErrorCode.OAUTH_TEMP_USER_SERIALIZATION_FAILED);
        }
    }

    public OAuthUserInfo getOAuthTempUser(UUID tempId) {
        String key = OAUTH_TEMP_KEY_PREFIX + tempId;
        String value = stringRedisTemplate.opsForValue().get(key);

        if (value == null) {
            throw new AuthException(AuthErrorCode.OAUTH_TEMP_USER_NOT_FOUND);
        }

        try {
            return objectMapper.readValue(value, OAuthUserInfo.class);
        } catch (JsonProcessingException e) {
            throw new AuthException(AuthErrorCode.OAUTH_TEMP_USER_SERIALIZATION_FAILED);
        }
    }

    public void deleteOAuthTempUser(UUID tempId) {
        String key = OAUTH_TEMP_KEY_PREFIX + tempId;
        stringRedisTemplate.delete(key);
    }


}
