package com.node5.memberservice.inquiry.application.dto;

import com.node5.memberservice.inquiry.domain.InquiryAnswer;

import java.util.UUID;

public record InquiryAnswerResponse(
        UUID id,
        String message
) {
    public static InquiryAnswerResponse from(InquiryAnswer inquiryAnswer) {
        return new InquiryAnswerResponse(inquiryAnswer.getId(), inquiryAnswer.getMessage());
    }
}
