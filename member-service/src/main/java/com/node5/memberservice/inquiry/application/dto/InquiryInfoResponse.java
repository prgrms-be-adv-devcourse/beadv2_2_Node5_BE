package com.node5.memberservice.inquiry.application.dto;

import com.node5.memberservice.inquiry.domain.Inquiry;

import java.time.LocalDateTime;
import java.util.UUID;

public record InquiryInfoResponse(
        UUID id,
        UUID memberId,
        String title,
        String message,
        String inquiryCategory,
        String status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,

        InquiryAnswerResponse inquiryAnswer
) {
    public static InquiryInfoResponse from(Inquiry inquiry, InquiryAnswerResponse inquiryAnswer) {
        return new InquiryInfoResponse(
                inquiry.getId(),
                inquiry.getMemberId(),
                inquiry.getTitle(),
                inquiry.getMessage(),
                inquiry.getInquiryCategory().name(),
                inquiry.getStatus().name(),
                inquiry.getCreatedAt(),
                inquiry.getModifiedAt(),
                inquiryAnswer
        );
    }
}
