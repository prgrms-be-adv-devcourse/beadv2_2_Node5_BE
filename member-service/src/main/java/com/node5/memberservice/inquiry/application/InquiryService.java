package com.node5.memberservice.inquiry.application;

import com.node5.memberservice.inquiry.application.dto.InquiryAnswerResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryInfoResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryRegisterCommand;
import com.node5.memberservice.inquiry.domain.*;
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
    private final InquiryAnswerRepository inquiryAnswerRepository;

    public Page<InquiryListResponse> getMyInquiryList(UUID memberId, Pageable pageable) {
        Page<Inquiry> inquiries = inquiryRepository.findAllByMemberId(memberId, pageable);
        return inquiries.map(InquiryListResponse::from);
    }

    public InquiryInfoResponse getMyInquiryInfo(UUID inquiryId, UUID memberId) {
        Inquiry inquiry = inquiryRepository.findByIdAndMemberId(inquiryId, memberId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );
        InquiryAnswerResponse inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiry.getId())
                .map(InquiryAnswerResponse::from)
                .orElse(null);

        return InquiryInfoResponse.from(inquiry, inquiryAnswer);
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

        inquiryAnswerRepository.deleteByInquiryId(inquiryId);
        inquiryRepository.delete(inquiry);
    }

    public Page<InquiryListResponse> getInquiryListForAdmin(InquiryStatus status, Pageable pageable) {
        Page<Inquiry> inquiries;
        if (status == null) {
            inquiries = inquiryRepository.findAll(pageable);
        } else {
            inquiries = inquiryRepository.findAllByStatus(status, pageable);
        }
        return inquiries.map(InquiryListResponse::from);
    }

    public InquiryInfoResponse getInquiryInfoForAdmin(UUID inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );
        InquiryAnswerResponse inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiry.getId())
                .map(InquiryAnswerResponse::from)
                .orElse(null);

        return InquiryInfoResponse.from(inquiry, inquiryAnswer);
    }
}
