package com.node5.memberservice.inquiry.presentation.dto;

import com.node5.memberservice.inquiry.application.dto.InquiryCommand;
import com.node5.memberservice.inquiry.domain.InquiryCategory;
import com.node5.memberservice.inquiry.exception.InquiryErrorCode;
import com.node5.memberservice.inquiry.exception.InquiryException;

import java.util.Arrays;

public record InquiryRequest(
        String title,
        String message,
        String inquiryCategory
) {
    public InquiryCommand toCommand() {
        InquiryCategory inquiryCategory = Arrays.stream(InquiryCategory.values())
                .filter(r -> r.name().equalsIgnoreCase(this.inquiryCategory))
                .findFirst()
                .orElseThrow(() -> new InquiryException(InquiryErrorCode.INVALID_CATEGORY));
        return new InquiryCommand(title, message, inquiryCategory);
    }
}
