package com.node5.orderservice.order.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.exception.ExceptionResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoderUtil {

    private final ObjectMapper objectMapper;

    public String getFeignErrorMessage(FeignException e) {
        ExceptionResponseDto err = parseFeignError(e);
        if (err != null) {
            return err.message();
        }
        return " (status:" + e.status() + ", raw:" + safeRawBody(e) + ")";
    }

    private ExceptionResponseDto parseFeignError(FeignException e) {
        return e.responseBody()
                .map(body -> {
                    try {
                        return objectMapper.readValue(e.contentUTF8(), ExceptionResponseDto.class);
                    } catch (Exception ex) {
                        return null;
                    }
                }).orElse(null);
    }

    private String safeRawBody(FeignException e) {
        try {
            return e.contentUTF8().isEmpty() ? "<empty>" : e.contentUTF8();
        } catch (Exception ex) {
            return "<unreadable>";
        }
    }
}
