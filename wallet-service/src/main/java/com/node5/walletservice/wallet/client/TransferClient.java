package com.node5.walletservice.wallet.client;

import com.node5.walletservice.wallet.client.dto.TransferRequset;
import com.node5.walletservice.wallet.client.dto.TransferResponse;

public interface TransferClient {
    TransferResponse executeTransfer(TransferRequset request);
}
