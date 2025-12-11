package com.node5.orderservice;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @Operation(
            summary = "루트 경로 테스트",
            description = "루트 경로(\"/\")로 들어오는 GET 요청을 처리하고, \"health check\" 문자열을 반환한다"
    )
    @GetMapping("/")
    public String test(){
        return "health check";
    }
}

