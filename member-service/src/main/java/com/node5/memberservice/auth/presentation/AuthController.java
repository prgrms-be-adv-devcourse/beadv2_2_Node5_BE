package com.node5.memberservice.auth.presentation;

import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.application.dto.TokenResponse;
import com.node5.memberservice.auth.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<LoginInfo> oAuthLogin(@RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.toCommand()));
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 회원가입을 처리합니다.")
    @PostMapping("/oauth/register")
    public ResponseEntity<LoginInfo> register(@RequestBody OAuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request.toCommand()));
    }

    @Operation(summary = "이메일 인증 코드 전송", description = "입력한 이메일로 인증 코드를 전송합니다.")
    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmailVerificationCode(@RequestBody SendEmailVerificationRequest request) {
        authService.sendEmailVerificationCode(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "입력한 이메일과 인증 코드를 확인합니다.")
    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.toCommand()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Member-Id") UUID memberId) {
        authService.logout(memberId);
        return ResponseEntity.ok().build();
    }
}
