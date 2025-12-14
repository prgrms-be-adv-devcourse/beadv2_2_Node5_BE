package com.node5.memberservice.inquiry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository {
    Page<Inquiry> findAll(Pageable pageable);
    Page<Inquiry> findAllByMemberId(UUID memberId, Pageable pageable);
    Optional<Inquiry> findById(UUID id);
    void save(Inquiry inquiry);
    Optional<Inquiry> findByIdAndMemberId(UUID inquiryId, UUID memberId);
    void deleteByIdAndMemberId(UUID inquiryId, UUID memberId);
}
