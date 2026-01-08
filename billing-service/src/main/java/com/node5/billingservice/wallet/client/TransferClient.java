package com.node5.billingservice.wallet.client;

import com.node5.billingservice.wallet.client.dto.TransferRequset;
import com.node5.billingservice.wallet.client.dto.TransferResponse;

public interface TransferClient {
    TransferResponse executeTransfer(TransferRequset request);
}
