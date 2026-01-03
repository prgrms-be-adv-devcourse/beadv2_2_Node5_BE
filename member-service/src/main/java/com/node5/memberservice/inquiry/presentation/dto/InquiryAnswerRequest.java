package com.node5.memberservice.inquiry.presentation.dto;

import com.node5.memberservice.inquiry.application.dto.InquiryAnswerCommand;

public record InquiryAnswerRequest(
        String message
) {
    public InquiryAnswerCommand toCommand() {
        return new InquiryAnswerCommand(message);
    }
}
