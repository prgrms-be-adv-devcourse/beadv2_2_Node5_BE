package com.node5.memberservice.inquiry.presentation.dto;

import com.node5.memberservice.inquiry.application.dto.InquiryCommand;
import com.node5.memberservice.inquiry.domain.InquiryCategory;
import com.node5.memberservice.inquiry.exception.InquiryErrorCode;
import com.node5.memberservice.inquiry.exception.InquiryException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Arrays;

public record InquiryRequest(
        @Size(max = 100, message = "message는 100자를 초과할 수 없습니다.")
        @NotBlank(message = "title은 필수입니다.")
        String title,
        @NotBlank(message = "message는 필수입니다.")
        String message,
        @Size(max = 100, message = "inquiryCategory는 100자를 초과할 수 없습니다.")
        @NotBlank(message = "inquiryCategory는 필수 입니다.")
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
