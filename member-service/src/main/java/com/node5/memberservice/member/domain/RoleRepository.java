package com.node5.memberservice.member.domain;

import java.util.List;

public interface RoleRepository {
    List<Role> findAll();
}
