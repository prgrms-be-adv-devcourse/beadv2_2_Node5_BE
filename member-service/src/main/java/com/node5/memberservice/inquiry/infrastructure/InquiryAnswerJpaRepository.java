package com.node5.memberservice.inquiry.infrastructure;

import com.node5.memberservice.inquiry.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InquiryAnswerJpaRepository extends JpaRepository<InquiryAnswer, UUID> {
    void deleteByInquiryId(UUID inquiryId);

    Optional<InquiryAnswer> findByInquiryId(UUID inquiryId);
}
