package com.node5.memberservice.auth.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements BaseErrorCode {
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST.value(), "AUTH_001", "지원하지 않는 OAuth provider 입니다."),
    INVALID_TEMP_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_002", "유효하지 않은 임시 토큰입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST.value(), "AUTH_003", "이메일이 인증되지 않았습니다."),
    EMAIL_CODE_MISMATCH(HttpStatus.BAD_REQUEST.value(), "AUTH_004", "인증 코드가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED.value(), "AUTH_005", "Refresh Token이 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "AUTH_006", "존재하지 않는 회원입니다."),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "AUTH_007", "잘못된 memberId 형식입니다."),
    OAUTH_ALREADY_CONNECTED(HttpStatus.CONFLICT.value(), "AUTH_008", "이미 연결된 OAuth 정보입니다."),

    OAUTH_TOKEN_ERROR(HttpStatus.BAD_GATEWAY.value(), "AUTH_009", "카카오 OAuth 토큰 발급 실패"),
    OAUTH_USERINFO_ERROR(HttpStatus.BAD_GATEWAY.value(), "AUTH_010", "소셜 로그인 사용자 정보 조회 실패"),
    OAUTH_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY.value(), "AUTH_011", "카카오 OAuth 응답이 유효하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    AuthErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
