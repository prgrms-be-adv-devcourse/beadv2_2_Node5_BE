package com.node5.memberservice.member.application;

import com.node5.memberservice.member.application.dto.RoleResponse;
import com.node5.memberservice.member.domain.Role;
import com.node5.memberservice.member.domain.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleResponse getRoles() {
        List<Role> roles = roleRepository.findAll();
        return RoleResponse.from(roles);
    }
}
