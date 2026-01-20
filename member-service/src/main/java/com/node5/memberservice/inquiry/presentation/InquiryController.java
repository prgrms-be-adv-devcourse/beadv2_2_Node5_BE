package com.node5.memberservice.inquiry.presentation;

import com.node5.memberservice.inquiry.application.InquiryService;
import com.node5.memberservice.inquiry.application.dto.InquiryInfoResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.presentation.dto.InquiryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<Page<InquiryListResponse>> getMyInquiryList(
            @RequestHeader("Member-Id") UUID memberId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(inquiryService.getMyInquiryList(memberId, pageable));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryInfoResponse> getMyInquiryInfo(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID inquiryId
    ) {
        return ResponseEntity.ok(inquiryService.getMyInquiryInfo(inquiryId, memberId));
    }

    @PostMapping
    public ResponseEntity<Void> createInquiry(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody InquiryRequest request
    ) {
        inquiryService.createInquiry(memberId, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{inquiryId}")
    public ResponseEntity<Void> modifyInquiry(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID inquiryId,
            @Valid @RequestBody InquiryRequest request
    ) {
        inquiryService.modifyInquiry(memberId, inquiryId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID inquiryId
    ) {
        inquiryService.deleteInquiry(memberId, inquiryId);
        return ResponseEntity.ok().build();
    }

}
