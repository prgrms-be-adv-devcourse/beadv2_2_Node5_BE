package com.node5.shopservice.shop.client;

import com.node5.common.domain.ApiResponseDto;
import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {

    @PostMapping("/internal/members/{memberId}/roles")
    ResponseEntity<ApiResponseDto<String>> modifyMemberRoles(@PathVariable UUID memberId, @RequestBody RoleModifyRequest request);

}
