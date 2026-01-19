package com.node5.paymentservice.payment.client.tossPayments;

import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.application.dto.PaymentConfirmCommand;
import com.node5.paymentservice.payment.application.dto.PaymentCancelCommand;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.TOSS_SECRET_KEY_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URL = "https://api.tosspayments.com/v1/payments/%s/cancel";

    private final RestTemplate restTemplate;
    @Value("${payment.toss.secret-key}")
    private String secretKey;

    public TossPaymentResponse confirm(PaymentConfirmCommand command) throws HttpStatusCodeException {
        if (secretKey == null) {
            throw new PaymentException(TOSS_SECRET_KEY_NOT_FOUND);
        }
        //토스에 요청할 header
        HttpHeaders headers = createHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", command.paymentKey());
        body.put("orderId", command.orderId());
        body.put("amount", command.amount());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(CONFIRM_URL, entity, TossPaymentResponse.class);
    }

    public TossPaymentResponse cancel(PaymentCancelCommand command) throws HttpStatusCodeException {
        if (secretKey == null) {
            throw new PaymentException(TOSS_SECRET_KEY_NOT_FOUND);
        }

        HttpHeaders headers = createHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", "예치금 환불");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String cancelUrl = String.format(CANCEL_URL, command.paymentKey());
        return restTemplate.postForObject(cancelUrl, entity, TossPaymentResponse.class);
    }

    private HttpHeaders createHeaders() { //토스에 전달하는 header 값 생성
        HttpHeaders headers = new HttpHeaders(); //headers 생성
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = secretKey + ":";
        //secret key base64 Encode
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        //Authorization header에 인코딩 값 입력.
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        return headers;
    }

}
