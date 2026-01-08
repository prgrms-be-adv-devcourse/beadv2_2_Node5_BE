package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletTransferCommand;
import jakarta.validation.constraints.*;

public record WalletTransferRequest(
        //계좌번호는 7자리~14자리 연속된 숫자
        @NotBlank(message = "계좌번호는 필수입니다.")
        @Pattern(regexp = "^[0-9]*$", message = "계좌번호는 숫자만 포함해야 합니다.")
        @Size(min = 7, max = 14, message = "계좌번호는 7자리에서 14자리 사이여야 합니다.")
        String toAccountNo,
        @NotNull(message = "정산 금액은 필수입니다.")
        @Positive(message = "정산 금액은 0보다 커야 합니다.")
        Long transferAmount
) {
    public WalletTransferCommand toCommand() {
        return new WalletTransferCommand(toAccountNo, transferAmount);
    }
}
