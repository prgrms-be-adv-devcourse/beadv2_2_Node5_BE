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

    OAUTH_TOKEN_ERROR(HttpStatus.BAD_GATEWAY.value(), "AUTH_009", "OAuth 토큰 발급 실패"),
    OAUTH_USERINFO_ERROR(HttpStatus.BAD_GATEWAY.value(), "AUTH_010", "소셜 로그인 사용자 정보 조회 실패"),
    OAUTH_RESPONSE_INVALID(HttpStatus.BAD_GATEWAY.value(), "AUTH_011", "OAuth 응답이 유효하지 않습니다."),

    OAUTH_TEMP_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "AUTH_012", "OAuth 임시 사용자 정보가 존재하지 않습니다."),
    OAUTH_TEMP_USER_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AUTH_013", "OAuth 임시 사용자 처리 중 오류가 발생했습니다."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "AUTH_014", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "AUTH_015", "유효하지 않은 토큰입니다."),
    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED.value(), "AUTH_016", "토큰 타입이 올바르지 않습니다."),
    WALLET_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AUTH_017", "지갑 생성 중 오류가 발생했습니다."),

    INVALID_ROLE(HttpStatus.BAD_REQUEST.value(), "AUTH_018", "잘못된 role 값입니다."),

    MEMBER_NOT_ACTIVE(HttpStatus.UNAUTHORIZED.value(), "AUTH_019", "유효하지 않은 회원 상태입니다."),
    MEMBER_IS_DELETED(HttpStatus.FORBIDDEN.value(), "AUTH_020", "탈퇴한 회원입니다.");

    private final int status;
    private final String code;
    private final String message;

    AuthErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
