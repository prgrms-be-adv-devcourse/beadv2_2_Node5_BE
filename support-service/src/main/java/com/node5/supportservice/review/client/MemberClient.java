package com.node5.supportservice.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", contextId = "reviewMemberClient")
public interface MemberClient {

    @GetMapping("/internal/members/{memberId}/nickname")
    ResponseEntity<String> getMemberNickname(@PathVariable String memberId);

}