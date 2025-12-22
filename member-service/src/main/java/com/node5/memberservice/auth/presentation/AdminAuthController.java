package com.node5.memberservice.auth.presentation;

import com.node5.memberservice.auth.application.EndPointService;
import com.node5.memberservice.auth.application.dto.EndPointInfoResponse;
import com.node5.memberservice.auth.presentation.dto.EndPointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/admin")
public class AdminAuthController {

    private final EndPointService endPointService;

    @GetMapping("/end-points")
    public ResponseEntity<Page<EndPointInfoResponse>> getEndPointList(
            @PageableDefault(size = 20, page = 0, sort = {"pathPattern", "httpMethod"})
            Pageable pageable
    ) {
        return ResponseEntity.ok(endPointService.getEndPointList(pageable));
    }

    @PostMapping("/end-points")
    public ResponseEntity<Void> createEndPoint(@RequestBody EndPointRequest request) {
        endPointService.createEndPoint(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/end-points/{endPointId}")
    public ResponseEntity<Void> modifyEndPoint(@PathVariable UUID endPointId, @RequestBody EndPointRequest request) {
        endPointService.modifyEndPoint(endPointId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/end-points/{endPointId}")
    public ResponseEntity<Void> deleteEndPoint(@PathVariable UUID endPointId) {
        endPointService.deleteEndPoint(endPointId);
        return ResponseEntity.ok().build();
    }
}
