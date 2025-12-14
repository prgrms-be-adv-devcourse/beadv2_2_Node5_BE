package com.node5.memberservice.inquiry.presentation;

import com.node5.memberservice.inquiry.application.InquiryService;
import com.node5.memberservice.inquiry.application.dto.InquiryInfoResponse;
import com.node5.memberservice.inquiry.application.dto.InquiryListResponse;
import com.node5.memberservice.inquiry.presentation.dto.InquiryRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 관리자용 문의 목록 api admin용 controller 로 이동
//    @GetMapping
//    public ResponseEntity<Page<InquiryListResponse>> getInquiryListForAdmin(
//            @RequestHeader("Member-Roles") String memberRoles,
//            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable
//    ) {
//        if (!memberRoles.contains(MemberRole.ADMIN.name())) { // Todo - contains 로 권한 체크 X
//            throw new InquiryException(InquiryErrorCode.INQUIRY_FORBIDDEN);
//        }
//        return ResponseEntity.ok(inquiryService.getInquiryListForAdmin(memberRoles, pageable));
//    }

    // 회원용 자기 문의 목록 api
    @GetMapping
    public ResponseEntity<Page<InquiryListResponse>> getInquiryListForMember(
            @RequestHeader("Member-Id") UUID memberId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(inquiryService.getInquiryListForMember(memberId, pageable));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryInfoResponse> getInquiryInfo(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestHeader("Member-Roles") String memberRoles,
            @PathVariable UUID inquiryId
    ) {
        return ResponseEntity.ok(inquiryService.getInquiryInfo(inquiryId, memberId, memberRoles));
    }

    @PostMapping
    public ResponseEntity<Void> createInquiry(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody InquiryRegisterRequest inquiryRegisterRequest
    ) {
        inquiryService.createInquiry(memberId, inquiryRegisterRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{inquiryId}")
    public ResponseEntity<Void> modifyInquiry(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID inquiryId,
            @RequestBody InquiryRegisterRequest inquiryRegisterRequest
    ) {
        inquiryService.modifyInquiry(memberId, inquiryId, inquiryRegisterRequest.toCommand());
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
