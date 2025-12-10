package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.*;
import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.oauth.OAuthProviderService;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final OAuthRepository oAuthRepository;
    private final MemberRepository memberRepository;
    private final Map<String, OAuthProviderService> providerMap;
    private final JwtProvider jwtProvider;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String emailSender;

    private final String EMAIL_VERIFY_CODE_KEY_PREFIX = "email:verify:";
    private final String VERIFIED_EMAIL_KEY_PREFIX = "email:verified:";


    public AuthService(
            OAuthRepository oAuthRepository, MemberRepository memberRepository,
            List<OAuthProviderService> providerList,
            JwtProvider jwtProvider, JavaMailSender mailSender, StringRedisTemplate stringRedisTemplate
    ) {
        this.oAuthRepository = oAuthRepository;
        this.memberRepository = memberRepository;
        this.providerMap = providerList.stream().collect(Collectors.toMap(OAuthProviderService::getProviderName, provider -> provider));
        this.jwtProvider = jwtProvider;
        this.mailSender = mailSender;
        this.stringRedisTemplate = stringRedisTemplate;
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
        return LoginInfo.newMember(temporaryToken);
    }

    @Transactional
    public LoginInfo register(OAuthRegisterCommand command) {
        String email = command.email();
        if (!isVerified(email)) {
            throw new IllegalArgumentException("인증되지 않은 이메일입니다.");
        }
        OAuthUserInfo oAuthUserInfo = jwtProvider.getOAuthUserInfo(command.temporaryToken());
        Optional<Member> existMember = memberRepository.findByEmail(email);

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

        stringRedisTemplate.delete(VERIFIED_EMAIL_KEY_PREFIX + email);

        return LoginInfo.success(member, accessToken, refreshToken);
    }

    public void sendEmailVerificationCode(SendEmailVerificationCommand command) {
        jwtProvider.validateTemporaryToken(command.temporaryToken());
        String verificationCode = generateVerificationCode();
        saveVerificationCode(command.email(), verificationCode);
        SimpleMailMessage mailMessage = createMailMessage(command.email(), verificationCode);
        mailSender.send(mailMessage);
    }

    public void verifyEmail(VerifyEmailCommand command) {
        if (!verifyCode(command.email(), command.verificationCode())) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        markVerified(command.email());
        stringRedisTemplate.delete(EMAIL_VERIFY_CODE_KEY_PREFIX + command.email());
    }

    private SimpleMailMessage createMailMessage(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MyRoutine] 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        message.setFrom(emailSender);
        return message;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1_000_000); // 0 ~ 999999
        return String.format("%06d", code);   // 항상 6자리로 패딩
    }

    private void saveVerificationCode(String email, String code) {
        stringRedisTemplate.opsForValue().set(EMAIL_VERIFY_CODE_KEY_PREFIX + email, code, Duration.ofMinutes(10));
    }

    private boolean verifyCode(String email, String code) {
        String stored = stringRedisTemplate.opsForValue().get(EMAIL_VERIFY_CODE_KEY_PREFIX + email);
        return code.equals(stored);
    }

    private void markVerified(String email) {
        stringRedisTemplate.opsForValue()
                .set(VERIFIED_EMAIL_KEY_PREFIX + email, "true", Duration.ofMinutes(10));
    }

    private boolean isVerified(String email) {
        return "true".equals(stringRedisTemplate.opsForValue()
                .get(VERIFIED_EMAIL_KEY_PREFIX + email));
    }
}
