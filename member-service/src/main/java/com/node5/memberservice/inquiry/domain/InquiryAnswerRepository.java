package com.node5.memberservice.inquiry.domain;

import java.util.Optional;
import java.util.UUID;

public interface InquiryAnswerRepository {
    Optional<InquiryAnswer> findByInquiryId(UUID inquiryId);
    void deleteByInquiryId(UUID inquiryId);
    void save(InquiryAnswer inquiryAnswer);
}
