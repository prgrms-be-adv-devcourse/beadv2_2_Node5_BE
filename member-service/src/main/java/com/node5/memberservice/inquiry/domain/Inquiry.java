package com.node5.memberservice.inquiry.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.memberservice.inquiry.application.dto.InquiryCommand;
import com.node5.memberservice.inquiry.exception.InquiryErrorCode;
import com.node5.memberservice.inquiry.exception.InquiryException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"inquiry\"", schema = "member")
public class Inquiry extends BaseEntity {
    @Id
    private UUID id;

    @JoinColumn(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_category", nullable = false, length = 100)
    private InquiryCategory inquiryCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryStatus status;

    private Inquiry(UUID id, UUID memberId, String title, String message, InquiryCategory inquiryCategory, InquiryStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.message = message;
        this.inquiryCategory = inquiryCategory;
        this.status = status;
    }

    public static Inquiry create(UUID memberId, InquiryCommand command) {
        return new Inquiry(
                UUID.randomUUID(),
                memberId,
                command.title(),
                command.message(),
                command.inquiryCategory(),
                InquiryStatus.RECEIVED
        );
    }

    public void modify(InquiryCommand command) {
        if (this.status != InquiryStatus.RECEIVED) {
            throw new InquiryException(InquiryErrorCode.INQUIRY_ALREADY_PROCESSED);
        }
        this.title = command.title();
        this.message = command.message();
        this.inquiryCategory = command.inquiryCategory();
    }

    public void inquiryAnswered() {
        this.status = InquiryStatus.ANSWERED;
    }
}
