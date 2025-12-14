package com.node5.memberservice.inquiry.application.dto;

import com.node5.memberservice.inquiry.domain.Inquiry;

import java.util.UUID;

public record InquiryListResponse(
        UUID id,
        String title,
        String inquiryCategory
) {
    public static InquiryListResponse from(Inquiry inquiry) {
        return new InquiryListResponse(inquiry.getId(), inquiry.getTitle(), inquiry.getInquiryCategory().name());
    }
}
