package com.node5.memberservice.inquiry.infrastructure;

import com.node5.memberservice.inquiry.domain.InquiryAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InquiryAnswerRepositoryAdaptor implements InquiryAnswerRepository {
    private final InquiryAnswerJpaRepository inquiryAnswerJpaRepository;

    @Override
    public void deleteByInquiryId(UUID inquiryId) {
        inquiryAnswerJpaRepository.deleteByInquiryId(inquiryId);
    }
}
