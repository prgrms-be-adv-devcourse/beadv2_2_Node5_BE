package com.node5.memberservice.auth.util;

import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    @Getter
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${token.secret}") String secret,
            @Value("${token.expiration.access}") long accessTokenExpiration,
            @Value("${token.expiration.refresh}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(JwtMemberInfo memberInfo) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpiration);
        return Jwts.builder()
                .subject(memberInfo.memberId())
                .claim("memberRoles", memberInfo.memberRoles())
                .claim("memberStatus", memberInfo.memberStatus())
                .claim("type", TokenType.ACCESS.name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(JwtMemberInfo memberInfo) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshTokenExpiration);
        return Jwts.builder()
                .subject(memberInfo.memberId())
                .claim("type", TokenType.REFRESH.name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }


    public String generateTemporaryToken(UUID tempId) {
        long temporaryTokenExpiration = 10 * 60 * 1000; // 10분
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + temporaryTokenExpiration);
        return Jwts.builder()
                .claim("tempId", tempId.toString())
                .claim("type", TokenType.TEMPORARY.name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    public Claims validateTokenType(String token, TokenType expectedType) {
        Claims claims = parseClaims(token);

        String actualType = claims.get("type", String.class);

        if (!expectedType.name().equals(actualType)) {
            throw new AuthException(AuthErrorCode.TOKEN_TYPE_MISMATCH);
        }
        return claims;
    }
}
