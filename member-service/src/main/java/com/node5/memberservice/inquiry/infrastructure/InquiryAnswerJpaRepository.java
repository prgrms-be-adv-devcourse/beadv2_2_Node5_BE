package com.node5.memberservice.inquiry.infrastructure;

import com.node5.memberservice.inquiry.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InquiryAnswerJpaRepository extends JpaRepository<InquiryAnswer, UUID> {
}
