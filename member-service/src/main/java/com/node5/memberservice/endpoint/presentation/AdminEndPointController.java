package com.node5.memberservice.endpoint.presentation;

import com.node5.memberservice.endpoint.presentation.dto.EndPointRequest;
import com.node5.memberservice.endpoint.application.EndPointService;
import com.node5.memberservice.endpoint.application.dto.EndPointInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/admin/endpoints")
public class AdminEndPointController {

    private final EndPointService endPointService;

    @GetMapping
    public ResponseEntity<Page<EndPointInfoResponse>> getEndPointList(
            @PageableDefault(size = 20, page = 0, sort = {"pathPattern", "httpMethod"})
            Pageable pageable
    ) {
        return ResponseEntity.ok(endPointService.getEndPointList(pageable));
    }

    @PostMapping
    public ResponseEntity<Void> createEndPoint(@RequestBody EndPointRequest request) {
        endPointService.createEndPoint(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{endPointId}")
    public ResponseEntity<Void> modifyEndPoint(@PathVariable UUID endPointId, @RequestBody EndPointRequest request) {
        endPointService.modifyEndPoint(endPointId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{endPointId}")
    public ResponseEntity<Void> deleteEndPoint(@PathVariable UUID endPointId) {
        endPointService.deleteEndPoint(endPointId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cache/refresh")
    public ResponseEntity<Void> refreshCache() {
        endPointService.refreshCache();
        return ResponseEntity.ok().build();
    }
}
