package com.node5.memberservice.auth.presentation;

import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.application.dto.LoginInfoResponse;
import com.node5.memberservice.auth.application.dto.TokenResponse;
import com.node5.memberservice.auth.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OAuth 로그인", description = "OAuth 로그인을 처리합니다.")
    @PostMapping("/oauth/login")
    public ResponseEntity<LoginInfoResponse> oAuthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.toCommand()));
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 회원가입을 처리합니다.")
    @PostMapping("/oauth/register")
    public ResponseEntity<LoginInfoResponse> register(@Valid @RequestBody OAuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request.toCommand()));
    }

    @Operation(summary = "이메일 인증 코드 전송", description = "입력한 이메일로 인증 코드를 전송합니다.")
    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody SendEmailVerificationRequest request) {
        authService.sendEmailVerificationCode(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "입력한 이메일과 인증 코드를 확인합니다.")
    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 리프레시", description = "accessToken과 refreshToken을 재발급합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.toCommand()));
    }

    @Operation(summary = "로그아웃", description = "저장한 refreshToken을 삭제해 로그아웃처리 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @RequestHeader("Member-Id") UUID memberId) {
        authService.logout(memberId);
        return ResponseEntity.ok().build();
    }
}
