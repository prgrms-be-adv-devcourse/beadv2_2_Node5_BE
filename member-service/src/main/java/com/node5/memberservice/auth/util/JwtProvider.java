package com.node5.memberservice.auth.util;

import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.application.dto.OAuthUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
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
                .claim("role", memberInfo.memberRole())
                .claim("memberStatus", memberInfo.memberStatus())
                .claim("type", "access")
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
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }


    // Todo - redis가 현재 없어 회원가입을 위한 provider, providerId 를 토큰에 담아 전송
    public String generateTemporaryToken(OAuthUserInfo oAuthUserInfo) {
        long temporaryTokenExpiration = 10 * 60 * 1000; // 10분
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + temporaryTokenExpiration);
        return Jwts.builder()
                .claim("provider", oAuthUserInfo.provider())
                .claim("providerId", oAuthUserInfo.providerId())
                .claim("type", "temporary")
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (
                ExpiredJwtException expiredJwtException) {
            throw new JwtException("JWT expired");
        } catch (JwtException jwtException) {
            throw new JwtException("JWT error");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
