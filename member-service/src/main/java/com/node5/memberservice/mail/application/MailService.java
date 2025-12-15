package com.node5.memberservice.mail.application;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationMail(String to, String from, String verificationCode) {
        SimpleMailMessage mailMessage = createMailMessage(to, from,  verificationCode);
        mailSender.send(mailMessage);
    }

    private SimpleMailMessage createMailMessage(String to, String from, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MyRoutine] 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        message.setFrom(from);
        return message;
    }
}
