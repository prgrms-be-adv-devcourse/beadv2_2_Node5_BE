package com.node5.memberservice.inquiry.application;

import com.node5.memberservice.inquiry.application.dto.InquiryInfoResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryRegisterCommand;
import com.node5.memberservice.inquiry.domain.Inquiry;
import com.node5.memberservice.inquiry.domain.InquiryRepository;
import com.node5.memberservice.inquiry.domain.InquiryStatus;
import com.node5.memberservice.inquiry.exception.InquiryErrorCode;
import com.node5.memberservice.inquiry.exception.InquiryException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public Page<InquiryListResponse> getInquiryListForMember(UUID memberId, Pageable pageable) {
        Page<Inquiry> inquiries = inquiryRepository.findAllByMemberId(memberId, pageable);
        return inquiries.map(InquiryListResponse::from);
    }

    public InquiryInfoResponse getInquiryInfo(UUID inquiryId, UUID memberId) {
        Inquiry inquiry = inquiryRepository.findByIdAndMemberId(inquiryId, memberId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );

        return InquiryInfoResponse.from(inquiry);
    }

    @Transactional
    public void createInquiry(UUID memberId, InquiryRegisterCommand command) {
        Inquiry inquiry = Inquiry.create(memberId, command);
        inquiryRepository.save(inquiry);
    }

    @Transactional
    public void modifyInquiry(UUID memberId, UUID inquiryId, InquiryRegisterCommand command) {
        Inquiry inquiry = inquiryRepository.findByIdAndMemberId(inquiryId, memberId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );
        inquiry.modify(command);
    }

    @Transactional
    public void deleteInquiry(UUID memberId, UUID inquiryId) {
        Inquiry inquiry = inquiryRepository.findByIdAndMemberId(inquiryId, memberId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );
        if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
            throw new InquiryException(InquiryErrorCode.INQUIRY_ALREADY_ANSWERED);
        }

        // Todo - 답변도 삭제
        inquiryRepository.delete(inquiry);
    }
}
