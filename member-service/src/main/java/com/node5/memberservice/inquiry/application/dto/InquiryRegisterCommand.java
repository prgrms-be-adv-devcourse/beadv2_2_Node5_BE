package com.node5.memberservice.inquiry.application.dto;

import com.node5.memberservice.inquiry.domain.InquiryCategory;

public record InquiryRegisterCommand(
        String title,
        String message,
        InquiryCategory inquiryCategory
) {
}
