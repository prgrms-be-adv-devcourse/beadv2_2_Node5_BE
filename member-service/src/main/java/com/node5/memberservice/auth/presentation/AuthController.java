package com.node5.memberservice.auth.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.presentation.dto.OAuthLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponseDto<LoginInfo>> oAuthLogin(@RequestBody OAuthLoginRequest request) {
        return authService.login(request);
    }

}
