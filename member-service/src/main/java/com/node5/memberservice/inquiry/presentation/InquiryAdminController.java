package com.node5.memberservice.inquiry.presentation;

import com.node5.memberservice.inquiry.application.InquiryService;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.domain.InquiryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class InquiryAdminController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<Page<InquiryListResponse>> getInquiryList(
            @RequestParam(required = false) InquiryStatus status,
            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable
    ){
        return ResponseEntity.ok(inquiryService.getInquiryList(status, pageable));
    }

}
