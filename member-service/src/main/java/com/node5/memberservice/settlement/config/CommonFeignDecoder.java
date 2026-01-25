package com.node5.memberservice.settlement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.exception.ExceptionResponseDto;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class CommonFeignDecoder implements ErrorDecoder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ErrorDecoder DEFAULT_DECODER = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.body() == null) {
            return new Default().decode(methodKey, response);
        }

        try (InputStream inputStream = response.body().asInputStream()) {
            // 공통 에러 response dto로 파싱
            ExceptionResponseDto errorBody = OBJECT_MAPPER.readValue(inputStream, ExceptionResponseDto.class);

            String msg = String.format("%s (%s)", errorBody.message(), errorBody.code());
            return FeignException.errorStatus(msg, response);
        } catch (IOException e) {
            Exception ex = DEFAULT_DECODER.decode(methodKey, response);
            String msg = String.format("Feign 에러 응답 파싱 실패 - %s", ex.getMessage());
            return FeignException.errorStatus(msg, response);
        }

    }
}
