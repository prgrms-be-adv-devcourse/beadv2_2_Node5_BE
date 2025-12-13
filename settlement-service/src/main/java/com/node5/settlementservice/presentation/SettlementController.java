package com.node5.settlementservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final JobLauncher jobLauncher;
    private final Job shopSettlementJob;

    @Operation(summary = "전체 판매자 월별 정산 배치 실행", description = "지정 연도/월 또는 지난달(default) 기준 모든 판매자(Shop)에 대해 정산 배치 Job을 실행한다.")
    @PostMapping("/internal/settlements/run-all")
    public ResponseEntity<String> runAll(
            @RequestParam(value = "yearMonth", required = false) String yearMonth
    ) throws Exception {

        JobParameters params = getDefaultSettlementParamsBuilder(yearMonth)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(shopSettlementJob, params);

        return ResponseEntity.ok(String.format("Settlement job started for all shops - BatchId: %s", jobExecution.getId()));
    }

    @Operation(summary = "특정 판매자 월별 정산 배치 실행", description = "지정 연도/월 또는 지난달(default) 기준 특정 판매자(Shop)에 대해 정산 배치 Job을 실행한다.")
    @PostMapping("/internal/settlements/run-shop")
    public ResponseEntity<String> runShop(
            @RequestParam("shopId") String shopId,
            @RequestParam(value = "yearMonth", required = false) String yearMonth
    ) throws Exception {

        JobParameters params = getDefaultSettlementParamsBuilder(yearMonth)
                .addString("shopId", shopId)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(shopSettlementJob, params);

        return ResponseEntity.ok(String.format("Settlement job started for shop(%s) - BatchId: %s", shopId, jobExecution.getId()));
    }

    private JobParametersBuilder getDefaultSettlementParamsBuilder(String yearMonth){
        YearMonth targetMonth;
        if(yearMonth != null && !yearMonth.isEmpty()){
            targetMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        }else{
            // 지정하지 않을 경우 지난 달을 기본값으로 설정
            targetMonth = YearMonth.now().minusMonths(1);
        }

        // YearMonth 객체로 시작일, 종료일 계산
        String startDate = targetMonth.atDay(1).format(DateTimeFormatter.ISO_DATE);
        String endDate = targetMonth.atEndOfMonth().format(DateTimeFormatter.ISO_DATE);

        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("startDate", startDate)
                .addString("endDate", endDate);
    }

}
