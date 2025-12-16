package com.node5.memberservice.inquiry.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.memberservice.inquiry.application.dto.InquiryRegisterCommand;
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

    private Inquiry(UUID id, UUID memberId, String title, String message, InquiryCategory inquiryCategory) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.message = message;
        this.inquiryCategory = inquiryCategory;
    }

    public static Inquiry create(UUID memberId, InquiryRegisterCommand command) {
        return new Inquiry(
                UUID.randomUUID(),
                memberId,
                command.title(),
                command.message(),
                command.inquiryCategory()
        );
    }

    public void modify(InquiryRegisterCommand command) {
        this.title = command.title();
        this.message = command.message();
        this.inquiryCategory = command.inquiryCategory();
    }
}
