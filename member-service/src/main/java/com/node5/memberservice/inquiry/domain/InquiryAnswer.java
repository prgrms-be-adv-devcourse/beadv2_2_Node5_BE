package com.node5.memberservice.inquiry.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"inquiry_answer\"", schema = "member")
public class InquiryAnswer extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID inquiryId;

    @Column(name = "answered_admin_id", nullable = false)
    private UUID answeredAdminId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private InquiryAnswer(UUID id, UUID inquiryId, UUID answeredAdminId, String message) {
        this.id = id;
        this.inquiryId = inquiryId;
        this.answeredAdminId = answeredAdminId;
        this.message = message;
    }

    public static InquiryAnswer create(UUID inquiryId, UUID answeredAdminId, String message) {
        return new InquiryAnswer(UUID.randomUUID(), inquiryId, answeredAdminId, message);
    }
}
