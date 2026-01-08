package com.node5.billingservice.wallet.client;

public enum TransferStateCode {
    SUCCESS,
    BANK_TIMEOUT,
    BANK_MAINTENANCE,
    INVALID_ACCOUNT,
    SYSTEM_ERROR
}
