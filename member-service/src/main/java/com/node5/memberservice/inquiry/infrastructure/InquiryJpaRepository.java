package com.node5.memberservice.inquiry.infrastructure;

import com.node5.memberservice.inquiry.domain.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InquiryJpaRepository extends JpaRepository<Inquiry, UUID> {
    Page<Inquiry> findAllByMemberId(UUID memberId, Pageable pageable);
    Optional<Inquiry> findByIdAndMemberId(UUID inquiryId, UUID memberId);
    void deleteByIdAndMemberId(UUID inquiryId, UUID memberId);
}
