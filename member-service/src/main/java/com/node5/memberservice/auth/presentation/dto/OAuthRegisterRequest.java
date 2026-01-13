package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthRegisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OAuthRegisterRequest(
        @NotBlank(message = "temporaryToken은 필수 입니다.")
        String temporaryToken,
        @NotBlank(message = "email은 필수 입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "name은 필수 입니다.")
        String name,
        @NotBlank(message = "nickname은 필수 입니다.")
        String nickname,
        @NotBlank(message = "phoneNumber은 필수 입니다.")
        String phoneNumber,
        @NotBlank(message = "address는 필수 입니다.")
        String address
) {
    public OAuthRegisterCommand toCommand() {
        return new OAuthRegisterCommand(temporaryToken, email, name, nickname, phoneNumber, address);
    }
}
