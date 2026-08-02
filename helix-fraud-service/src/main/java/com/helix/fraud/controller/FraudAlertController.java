// Backs the Alerts & Fraud screen: list with tab filtering, and
// status updates (marking reviewed/blocked).

package com.helix.fraud.controller;

import com.helix.common.dto.ApiResponse;
import com.helix.fraud.dto.AlertStatusUpdateRequest;
import com.helix.fraud.dto.FraudAlertResponse;
import com.helix.fraud.service.FraudAlertService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;

    @Autowired
    public FraudAlertController(FraudAlertService fraudAlertService) {
        this.fraudAlertService = fraudAlertService;
    }

    // ?status=PENDING|REVIEWED|BLOCKED|ALL - matches the four screen tabs.
    @GetMapping
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAlerts(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        List<FraudAlertResponse> alerts = fraudAlertService.getAlerts(authentication.getName(), status);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PutMapping("/{alertId}/status")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> updateStatus(
            @PathVariable Long alertId,
            @Valid @RequestBody AlertStatusUpdateRequest request,
            Authentication authentication) {
        FraudAlertResponse updated = fraudAlertService.updateStatus(
                authentication.getName(), alertId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Alert status updated", updated));
    }
}