package com.node5.memberservice.inquiry.infrastructure;

import com.node5.memberservice.inquiry.domain.Inquiry;
import com.node5.memberservice.inquiry.domain.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryAdaptor implements InquiryRepository {
    private final InquiryJpaRepository inquiryJpaRepository;

    @Override
    public Page<Inquiry> findAllByMemberId(UUID memberId, Pageable pageable) {
        return inquiryJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public void save(Inquiry inquiry) {
        inquiryJpaRepository.save(inquiry);
    }

    @Override
    public Optional<Inquiry> findByIdAndMemberId(UUID inquiryId, UUID memberId) {
        return inquiryJpaRepository.findByIdAndMemberId(inquiryId, memberId);
    }

    @Override
    public void delete(Inquiry inquiry) {
        inquiryJpaRepository.delete(inquiry);
    }
}
