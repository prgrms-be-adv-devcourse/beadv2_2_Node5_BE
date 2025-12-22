package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.*;
import com.node5.memberservice.auth.domain.EndPointRepository;
import com.node5.memberservice.auth.domain.Endpoint;
import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.auth.oauth.OAuthProviderService;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.auth.util.TokenType;
import com.node5.memberservice.mail.application.MailService;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.redis.application.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final AntPathMatcher matcher = new AntPathMatcher();
    private volatile Map<EndpointKey, List<Endpoint>> allowedEndpointCache;

    private final OAuthRepository oAuthRepository;
    private final MemberRepository memberRepository;
    private final EndPointRepository endPointRepository;
    private final Map<String, OAuthProviderService> providerMap;
    private final JwtProvider jwtProvider;
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String sender;


    public AuthService(
            OAuthRepository oAuthRepository,
            MemberRepository memberRepository,
            EndPointRepository endPointRepository,
            List<OAuthProviderService> providerList,
            JwtProvider jwtProvider,
            MailService mailService,
            RedisService redisService
    ) {
        this.oAuthRepository = oAuthRepository;
        this.memberRepository = memberRepository;
        this.endPointRepository = endPointRepository;
        this.providerMap = providerList.stream().collect(Collectors.toMap(OAuthProviderService::getProviderName, provider -> provider));
        this.jwtProvider = jwtProvider;
        this.mailService = mailService;
        this.redisService = redisService;
    }

    @PostConstruct
    public void init() {
        refreshCache();
    }

    public void refreshCache() {
        this.allowedEndpointCache = loadAllowedEndpoint();
    }

    private Map<EndpointKey, List<Endpoint>> loadAllowedEndpoint() {
        Map<EndpointKey, List<Endpoint>> newCache = new HashMap<>();
        List<Endpoint> endpoints = endPointRepository.findAll();
        for (Endpoint endpoint : endpoints) {
            EndpointKey key = new EndpointKey(endpoint.getRole(), endpoint.getHttpMethod());
            newCache.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(endpoint);
        }

        return newCache.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())
                ));
    }

    public LoginInfoResponse login(OAuthLoginCommand command) {

        OAuthProviderService providerService = providerMap.get(command.provider());

        if (providerService == null) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER);
        }

        OAuthUserInfo oAuthUserInfo = providerService.getUserInfo(command);

        Optional<OAuth> oAuth = oAuthRepository.findByProviderAndProviderId(oAuthUserInfo.provider(), oAuthUserInfo.providerId());

        if (oAuth.isPresent()) {
            Member member = oAuth.get().getMember();
            JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
            String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
            String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
            redisService.saveRefreshToken(member.getId(), refreshToken);
            return LoginInfoResponse.success(member, accessToken, refreshToken);
        }
        UUID tempId = UUID.randomUUID();
        redisService.saveOAuthTempUser(tempId, oAuthUserInfo);
        String temporaryToken = jwtProvider.generateTemporaryToken(tempId);
        return LoginInfoResponse.newMember(temporaryToken);
    }

    @Transactional
    public LoginInfoResponse register(OAuthRegisterCommand command) {
        String email = command.email();
        if (!"true".equals(redisService.getVerifiedEmail(email))) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        Claims claims = jwtProvider.validateTokenType(command.temporaryToken(), TokenType.TEMPORARY);
        UUID tempId = UUID.fromString(claims.get("tempId", String.class));
        OAuthUserInfo oAuthUserInfo = redisService.getOAuthTempUser(tempId);

        Optional<Member> existMember = memberRepository.findByEmailAndDeletedAtIsNull(email);

        Member member = existMember.orElseGet(() -> memberRepository.save(Member.create(command)));

        Optional<OAuth> existOAuth = oAuthRepository.findByProviderAndMember(oAuthUserInfo.provider(), member);

        if (existOAuth.isPresent()) {
            OAuth oAuth = existOAuth.get();
            oAuth.modifyProviderId(oAuthUserInfo.providerId());
        } else {
            OAuth oAuth = OAuth.create(member, oAuthUserInfo);
            oAuthRepository.save(oAuth);
        }

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
        String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
        redisService.saveRefreshToken(member.getId(), refreshToken);

        redisService.deleteOAuthTempUser(tempId);
        redisService.deleteVerifiedEmail(email);

        return LoginInfoResponse.success(member, accessToken, refreshToken);
    }

    public void sendEmailVerificationCode(SendEmailVerificationCommand command) {
        jwtProvider.validateTokenType(command.temporaryToken(), TokenType.TEMPORARY);

        String verificationCode = generateVerificationCode();
        redisService.saveVerificationCode(command.email(), verificationCode);
        mailService.sendVerificationMail(command.email(), sender, verificationCode);
    }

    public void verifyEmail(VerifyEmailCommand command) {
        String stored = redisService.getVerificationCode(command.email());
        if (!command.verificationCode().equals(stored)) {
            throw new AuthException(AuthErrorCode.EMAIL_CODE_MISMATCH);
        }
        redisService.markVerifiedEmail(command.email());
        redisService.deleteVerificationCode(command.email());
    }

    public TokenResponse refreshToken(RefreshTokenCommand command) {
        String refreshToken = command.refreshToken();
        Claims claims = jwtProvider.validateTokenType(refreshToken, TokenType.REFRESH);
        String memberId = claims.getSubject();

        UUID memberUuid;
        try {
            memberUuid = UUID.fromString(memberId);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_ID);
        }

        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberUuid)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        String storedRefreshToken = redisService.getRefreshToken(member.getId());

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
        String newRefreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
        redisService.saveRefreshToken(member.getId(), newRefreshToken);

        return new TokenResponse(accessToken, newRefreshToken);
    }

    public void logout(UUID memberId) {
        redisService.deleteRefreshToken(memberId);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1_000_000); // 0 ~ 999999
        return String.format("%06d", code);   // 항상 6자리로 패딩
    }

    public boolean authorize(AuthorizeCommand command) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(command.memberId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        Set<MemberRole> roles = member.getRoles();

        // 스냅샷
        Map<EndpointKey, List<Endpoint>> currentCache = this.allowedEndpointCache;

        List<Endpoint> allowedEndpoints = roles.stream()
                .flatMap(role -> currentCache.getOrDefault(
                        new EndpointKey(role, command.method()),
                        List.of()
                ).stream())
                .distinct()
                .toList();

        // 필요한가?
        if (allowedEndpoints.isEmpty()) {
            allowedEndpoints = endPointRepository.findAllowedEndpoints(member.getRoles(), command.method());
        }

        if (allowedEndpoints.isEmpty()) {
            return false;
        }

        for (Endpoint endpoint : allowedEndpoints) {
            if (matcher.match(endpoint.getPathPattern(), command.path())) {
                return true;
            }
        }
        return false;
    }
}
