package com.node5.memberservice.inquiry.presentation.dto;

import com.node5.memberservice.inquiry.application.dto.InquiryAnswerCommand;
import jakarta.validation.constraints.NotBlank;

public record InquiryAnswerRequest(
        @NotBlank(message = "message는 필수입니다.")
        String message
) {
    public InquiryAnswerCommand toCommand() {
        return new InquiryAnswerCommand(message);
    }
}
