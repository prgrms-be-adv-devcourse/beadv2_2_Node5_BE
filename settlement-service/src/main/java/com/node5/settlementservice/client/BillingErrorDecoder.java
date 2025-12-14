package com.node5.settlementservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.exception.ExceptionResponseDto;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class BillingErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            try {
                ExceptionResponseDto errorBody = objectMapper.readValue(
                        response.body().asInputStream(),
                        ExceptionResponseDto.class
                );

                String detailedMessage = String.format(
                        "%s (%s)",
                        errorBody.message(), errorBody.code()
                );
                return FeignException.errorStatus(detailedMessage, response);
            } catch (IOException e) {
                String msg = "Billing Service 404 응답 파싱 실패";
                return feign.FeignException.errorStatus(msg, response);
            }
        }

        return new Default().decode(methodKey, response);
    }
}