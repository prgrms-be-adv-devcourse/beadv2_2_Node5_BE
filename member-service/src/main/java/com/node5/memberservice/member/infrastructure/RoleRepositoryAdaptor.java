package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.Role;
import com.node5.memberservice.member.domain.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdaptor implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll();
    }
}
