package com.node5.shopservice.shop.client;

import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import com.node5.shopservice.shop.client.dto.RoleModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {

    @PostMapping("/internal/members/{memberId}/roles")
    ResponseEntity<RoleModifyResponse> addMemberRole(@PathVariable UUID memberId, @RequestBody RoleModifyRequest request);

    @DeleteMapping("/internal/members/{memberId}/roles/{role}")
    ResponseEntity<RoleModifyResponse> deleteMemberRole(@PathVariable UUID memberId, @PathVariable String role);

}
