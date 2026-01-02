package com.node5.memberservice.inquiry.presentation;

import com.node5.memberservice.inquiry.application.InquiryService;
import com.node5.memberservice.inquiry.application.dto.InquiryInfoResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.domain.InquiryStatus;
import com.node5.memberservice.inquiry.presentation.dto.InquiryAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class InquiryAdminController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<Page<InquiryListResponse>> getInquiryListForAdmin(
            @RequestParam(required = false) InquiryStatus status,
            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable
    ){
        return ResponseEntity.ok(inquiryService.getInquiryListForAdmin(status, pageable));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryInfoResponse> getInquiryInfoForAdmin(@PathVariable UUID inquiryId){
        return ResponseEntity.ok(inquiryService.getInquiryInfoForAdmin(inquiryId));
    }

    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<Void> createInquiryAnswer(
            @PathVariable UUID inquiryId,
            @RequestHeader("Member-Id") UUID adminId,
            @RequestBody InquiryAnswerRequest request
    ){
        inquiryService.createInquiryAnswer(inquiryId, adminId, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
