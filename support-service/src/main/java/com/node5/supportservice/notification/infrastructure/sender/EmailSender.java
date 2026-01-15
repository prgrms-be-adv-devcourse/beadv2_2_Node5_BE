package com.node5.supportservice.notification.infrastructure.sender;

import com.node5.supportservice.notification.client.MemberClient;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.exception.NotificationErrorCode;
import com.node5.supportservice.notification.exception.NotificationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final MemberClient memberClient;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void send(NotificationMessage message) {
        SimpleMailMessage mailMessage = createMailMessage(message);
        mailSender.send(mailMessage);
    }

    private SimpleMailMessage createMailMessage(NotificationMessage message) {
        String memberEmail;
        try {
            memberEmail = memberClient.getMemberEmail(message.memberId()).getBody();
            if (memberEmail == null) {
                throw new NotificationException(NotificationErrorCode.MEMBER_EMAIL_NOT_FOUND);
            }
        } catch (FeignException.NotFound e) {
            throw new NotificationException(NotificationErrorCode.MEMBER_EMAIL_NOT_FOUND);
        } catch (FeignException e) {
            throw new NotificationException(NotificationErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(memberEmail);
        mail.setSubject(message.title());
        mail.setText(message.body());
        mail.setFrom(sender);
        return mail;
    }
}

