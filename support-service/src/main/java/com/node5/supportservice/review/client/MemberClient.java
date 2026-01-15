package com.node5.supportservice.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "member-service", contextId = "reviewMemberClient")
public interface MemberClient {

    @GetMapping("/internal/members/nickname")
    String getMemberNickname(@RequestHeader("Member-Id") UUID memberId);

}