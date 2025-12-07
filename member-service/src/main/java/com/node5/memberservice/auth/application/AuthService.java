package com.node5.memberservice.auth.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.oauth.OAuthProviderService;
import com.node5.memberservice.auth.presentation.dto.OAuthRegisterRequest;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final Map<String, OAuthProviderService> providerMap;
    private final JwtProvider jwtProvider;

    public AuthService(
            OAuthRepository oAuthRepository,
            List<OAuthProviderService> providerList,
            JwtProvider jwtProvider
    ) {
        this.oAuthRepository = oAuthRepository;
        this.providerMap = providerList.stream().collect(Collectors.toMap(OAuthProviderService::getProviderName, provider -> provider));
        this.jwtProvider = jwtProvider;
    }

    public ResponseEntity<ApiResponseDto<LoginInfo>> login(OAuthLoginCommand command) {

        OAuthProviderService providerService = providerMap.get(command.provider());

        if (providerService == null) {
            throw new IllegalArgumentException("Invalid provider: " + command.provider());
        }

        OAuthUserInfo oAuthUserInfo = providerService.getUserInfo(command.providerCode());

        Optional<OAuth> oAuth = oAuthRepository.findByProviderAndProviderId(oAuthUserInfo.provider(), oAuthUserInfo.providerId());

        if (oAuth.isPresent()) {
            Member member = oAuth.get().getMember();
            JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
            String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
            String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
            LoginInfo loginInfo = LoginInfo.success(member, accessToken, refreshToken);
            ApiResponseDto<LoginInfo> response = new ApiResponseDto<>(HttpStatus.OK.value(), "로그인 성공", loginInfo);
            return ResponseEntity.ok(response);
        }

        String temporaryToken = jwtProvider.generateTemporaryToken(oAuthUserInfo);
        LoginInfo loginInfo = LoginInfo.emailRequired(temporaryToken);
        ApiResponseDto<LoginInfo> response = new ApiResponseDto<>(HttpStatus.OK.value(), "이메일 필요", loginInfo);

        return ResponseEntity.ok(response);
    }

}
