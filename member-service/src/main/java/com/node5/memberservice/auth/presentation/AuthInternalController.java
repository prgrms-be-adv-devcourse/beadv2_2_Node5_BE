package com.node5.memberservice.auth.presentation;

import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.presentation.dto.AuthorizeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
public class AuthInternalController {

    private final AuthService authService;

    @PostMapping("/authorize")
    public boolean authorize(@RequestBody AuthorizeRequest request){
        return authService.authorize(request.toCommand());
    }
}
