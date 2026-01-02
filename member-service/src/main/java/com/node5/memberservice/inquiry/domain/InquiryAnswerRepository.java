package com.node5.memberservice.inquiry.domain;

import java.util.UUID;

public interface InquiryAnswerRepository {
    void deleteByInquiryId(UUID inquiryId);
}
