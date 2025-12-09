package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import com.node5.memberservice.auth.application.dto.OAuthRegisterCommand;
import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.oauth.OAuthProviderService;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final MemberRepository memberRepository;
    private final Map<String, OAuthProviderService> providerMap;
    private final JwtProvider jwtProvider;

    public AuthService(
            OAuthRepository oAuthRepository, MemberRepository memberRepository,
            List<OAuthProviderService> providerList,
            JwtProvider jwtProvider
    ) {
        this.oAuthRepository = oAuthRepository;
        this.memberRepository = memberRepository;
        this.providerMap = providerList.stream().collect(Collectors.toMap(OAuthProviderService::getProviderName, provider -> provider));
        this.jwtProvider = jwtProvider;
    }

    public LoginInfo login(OAuthLoginCommand command) {

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
            return LoginInfo.success(member, accessToken, refreshToken);
        }

        String temporaryToken = jwtProvider.generateTemporaryToken(oAuthUserInfo);
        return LoginInfo.emailRequired(temporaryToken);
    }

    public LoginInfo register(OAuthRegisterCommand command) {
        OAuthUserInfo oAuthUserInfo = jwtProvider.getOAuthUserInfo(command.temporaryToken());
        Optional<Member> existMember = memberRepository.findByEmail(command.email());

        Member member = null;

        if (existMember.isPresent()) {
            member = existMember.get();
        } else {
            Member newMember = Member.create(command.email(), command.name(), command.phoneNumber(), command.address());
            member = memberRepository.save(newMember);
        }

        OAuth oAuth = OAuth.create(member, oAuthUserInfo);
        oAuthRepository.save(oAuth);

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
        String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
        return LoginInfo.success(member, accessToken, refreshToken);
    }
}
