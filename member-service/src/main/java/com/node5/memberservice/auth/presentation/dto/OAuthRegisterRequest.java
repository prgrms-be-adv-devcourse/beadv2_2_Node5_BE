package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthRegisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OAuthRegisterRequest(
        @NotBlank(message = "temporaryToken은 필수 입니다.")
        String temporaryToken,
        @Size(max = 100, message = "email은 100자를 초과할 수 없습니다.")
        @NotBlank(message = "email은 필수 입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        String email,
        @Size(max = 20, message = "name은 20자를 초과할 수 없습니다.")
        @NotBlank(message = "name은 필수 입니다.")
        String name,
        @Size(max = 20, message = "nickname은 20자를 초과할 수 없습니다.")
        @NotBlank(message = "nickname은 필수 입니다.")
        String nickname,
        @Pattern(
                regexp = "^01[0-9]{8,9}$",
                message = "phoneNumber 형식이 올바르지 않습니다."
        )
        @NotBlank(message = "phoneNumber은 필수 입니다.")
        String phoneNumber,
        @Size(max = 100, message = "address는 100자를 초과할 수 없습니다.")
        @NotBlank(message = "address는 필수 입니다.")
        String address
) {
    public OAuthRegisterCommand toCommand() {
        return new OAuthRegisterCommand(temporaryToken, email, name, nickname, phoneNumber, address);
    }
}
