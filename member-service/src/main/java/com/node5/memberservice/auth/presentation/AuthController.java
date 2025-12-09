package com.node5.memberservice.auth.presentation;

import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.presentation.dto.OAuthLoginRequest;
import com.node5.memberservice.auth.presentation.dto.OAuthRegisterRequest;
import com.node5.memberservice.auth.presentation.dto.SendEmailVerificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OAuth 로그인", description = "OAuth 로그인을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/oauth/login")
    public ResponseEntity<LoginInfo> oAuthLogin(@RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.toCommand()));
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 회원가입을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/oauth/register")
    public ResponseEntity<LoginInfo> register(@RequestBody OAuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request.toCommand()));
    }

    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmailVerificationCode(@RequestBody SendEmailVerificationRequest request) {
        authService.sendEmailVerificationCode(request.toCommand());
        return ResponseEntity.ok().build();
    }
}
