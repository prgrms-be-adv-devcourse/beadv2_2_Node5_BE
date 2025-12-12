package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.*;
import com.node5.memberservice.auth.client.BillingClient;
import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.auth.oauth.OAuthProviderService;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.auth.util.TokenType;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import com.node5.memberservice.redis.application.RedisService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final MemberRepository memberRepository;
    private final Map<String, OAuthProviderService> providerMap;
    private final JwtProvider jwtProvider;
    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final BillingClient billingClient;

    @Value("${spring.mail.username}")
    private String sender;


    public AuthService(
            OAuthRepository oAuthRepository,
            MemberRepository memberRepository,
            List<OAuthProviderService> providerList,
            JwtProvider jwtProvider,
            JavaMailSender mailSender,
            RedisService redisService,
            BillingClient billingClient
    ) {
        this.oAuthRepository = oAuthRepository;
        this.memberRepository = memberRepository;
        this.providerMap = providerList.stream().collect(Collectors.toMap(OAuthProviderService::getProviderName, provider -> provider));
        this.jwtProvider = jwtProvider;
        this.mailSender = mailSender;
        this.redisService = redisService;
        this.billingClient = billingClient;
    }

    public LoginInfoResponse login(OAuthLoginCommand command) {

        OAuthProviderService providerService = providerMap.get(command.provider());

        if (providerService == null) {
            throw new AuthException(AuthErrorCode.INVALID_PROVIDER);
        }

        OAuthUserInfo oAuthUserInfo = providerService.getUserInfo(command.providerCode());

        Optional<OAuth> oAuth = oAuthRepository.findByProviderAndProviderId(oAuthUserInfo.provider(), oAuthUserInfo.providerId());

        if (oAuth.isPresent()) {
            Member member = oAuth.get().getMember();
            JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
            String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
            String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
            redisService.saveRefreshToken(member.getId(), refreshToken);
            return LoginInfoResponse.success(member, accessToken, refreshToken);
        }

        String temporaryToken = jwtProvider.generateTemporaryToken(oAuthUserInfo);
        return LoginInfoResponse.newMember(temporaryToken);
    }

    @Transactional
    public LoginInfoResponse register(OAuthRegisterCommand command) {
        String email = command.email();
        if (!"true".equals(redisService.getVerifiedEmail(email))) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        OAuthUserInfo oAuthUserInfo = jwtProvider.getOAuthUserInfo(command.temporaryToken());

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

        billingClient.createWallet(member.getId());

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
        String refreshToken = jwtProvider.generateRefreshToken(jwtMemberInfo);
        redisService.saveRefreshToken(member.getId(), refreshToken);

        redisService.deleteVerifiedEmail(email);

        return LoginInfoResponse.success(member, accessToken, refreshToken);
    }

    public void sendEmailVerificationCode(SendEmailVerificationCommand command) {
        jwtProvider.validateTokenType(command.temporaryToken(), TokenType.TEMPORARY);

        String verificationCode = generateVerificationCode();
        redisService.saveVerificationCode(command.email(), verificationCode);
        sendVerificationMail(command.email(), verificationCode);
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

    private SimpleMailMessage createMailMessage(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MyRoutine] 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        message.setFrom(sender);
        return message;
    }

    @Async
    public void sendVerificationMail(String email, String verificationCode) {
        SimpleMailMessage mailMessage = createMailMessage(email, verificationCode);
        mailSender.send(mailMessage);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1_000_000); // 0 ~ 999999
        return String.format("%06d", code);   // 항상 6자리로 패딩
    }
}
