package com.node5.memberservice.inquiry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository {
    Page<Inquiry> findAllByMemberId(UUID memberId, Pageable pageable);
    void save(Inquiry inquiry);
    Optional<Inquiry> findByIdAndMemberId(UUID inquiryId, UUID memberId);
    void delete(Inquiry inquiry);
}
