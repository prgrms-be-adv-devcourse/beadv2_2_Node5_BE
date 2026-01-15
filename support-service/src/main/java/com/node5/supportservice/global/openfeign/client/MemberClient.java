package com.node5.supportservice.global.openfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "member-service", contextId = "memberClient")
public interface MemberClient {

    @GetMapping("/internal/members/{memberId}/email")
    ResponseEntity<String> getMemberEmail(@PathVariable String memberId);

    @GetMapping("/internal/members/nickname")
    String getMemberNickname(@RequestHeader("Member-Id") UUID memberId);
}
