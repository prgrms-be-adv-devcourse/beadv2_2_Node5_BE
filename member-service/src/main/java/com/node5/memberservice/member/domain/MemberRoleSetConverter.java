package com.node5.memberservice.member.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class MemberRoleSetConverter implements AttributeConverter<Set<MemberRole>, String> {

    @Override
    public String convertToDatabaseColumn(Set<MemberRole> roles) {
        if (roles == null || roles.isEmpty()) return "";
        return roles.stream().map(MemberRole::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<MemberRole> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new HashSet<>();
        return Arrays.stream(dbData.split(",")).map(MemberRole::valueOf).collect(Collectors.toSet());
    }
}
