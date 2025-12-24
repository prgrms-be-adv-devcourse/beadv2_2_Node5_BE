package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<Role, String> {
}
