package com.node5.walletservice.wallet.exception;

import com.node5.common.exception.BaseException;

public class WalletException extends BaseException {
    public WalletException(WalletErrorCode walletErrorCode) {
        super(walletErrorCode);
    }
}
