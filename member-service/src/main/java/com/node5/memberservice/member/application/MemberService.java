package com.node5.memberservice.member.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.application.dto.MemberInfo;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import com.node5.memberservice.member.presentation.dto.MemberRegisterRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponseDto<MemberInfo>> findById(UUID memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        ApiResponseDto<MemberInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "회원 조회 성공", MemberInfo.from(member));
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<MemberInfo>> registerRequiredInfo(UUID memberId, MemberRegisterRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        member.registerRequiredInfo(request.name(), request.phoneNumber(), request.address());
        ApiResponseDto<MemberInfo> responseDto = new ApiResponseDto<>(HttpStatus.CREATED.value(), "회원 필수 정보 추가 성공", MemberInfo.from(member));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    public ResponseEntity<ApiResponseDto<?>> deleteMember(UUID memberId) {
        memberRepository.deleteById(memberId);
        ApiResponseDto<?> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "회원 삭제 성공", null);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> modifyMemberRoles(UUID memberId, RoleModifyCommand command) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        member.modifyRoles(command.role(), command.action());
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        String accessToken = jwtProvider.generateAccessToken(jwtMemberInfo);
        ApiResponseDto<String> response = new ApiResponseDto<>(HttpStatus.OK.value(), "로그인 성공", accessToken);
        return ResponseEntity.ok(response);
    }
}
