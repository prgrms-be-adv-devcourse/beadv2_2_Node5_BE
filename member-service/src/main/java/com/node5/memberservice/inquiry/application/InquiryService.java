package com.node5.memberservice.inquiry.application;

import com.node5.memberservice.inquiry.application.dto.*;
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
    public void createInquiry(UUID memberId, InquiryCommand command) {
        Inquiry inquiry = Inquiry.create(memberId, command);
        inquiryRepository.save(inquiry);
    }

    @Transactional
    public void modifyInquiry(UUID memberId, UUID inquiryId, InquiryCommand command) {
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
        if (inquiry.isStatus(InquiryStatus.ANSWERED)) {
            throw new InquiryException(InquiryErrorCode.INQUIRY_ALREADY_ANSWERED);
        }

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

    // Todo - lock 고려
    @Transactional
    public void createInquiryAnswer(UUID inquiryId, UUID adminId, InquiryAnswerCommand command) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );

        if(inquiry.isStatus(InquiryStatus.ANSWERED)) {
            throw new InquiryException(InquiryErrorCode.INQUIRY_ALREADY_ANSWERED);
        }

        InquiryAnswer inquiryAnswer = InquiryAnswer.create(inquiry.getId(), adminId, command.message());
        inquiryAnswerRepository.save(inquiryAnswer);
        inquiry.markAnswered();
    }

    // Todo - lock 고려
    @Transactional
    public void modifyInquiryAnswer(UUID inquiryId, UUID adminId, InquiryAnswerCommand command) {
        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_ANSWER_NOT_FOUND)
        );
        inquiryAnswer.modify(adminId, command);
    }

    @Transactional
    public void deleteInquiryAnswer(UUID inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        );

        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(
                () -> new InquiryException(InquiryErrorCode.INQUIRY_ANSWER_NOT_FOUND)
        );

        inquiryAnswerRepository.delete(inquiryAnswer);
        inquiry.markInProgress();
    }
}
