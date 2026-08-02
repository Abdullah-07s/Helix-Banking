// Backs the Alerts & Fraud screen: listing (with All/Pending/Reviewed/
// Blocked filtering) and status updates.

package com.helix.fraud.service;

import com.helix.common.exception.ResourceNotFoundException;
import com.helix.common.exception.UnauthorizedException;
import com.helix.fraud.dto.FraudAlertResponse;
import com.helix.fraud.entity.FraudAlert;
import com.helix.fraud.repository.FraudAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FraudAlertServiceImpl implements FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;

    @Autowired
    public FraudAlertServiceImpl(FraudAlertRepository fraudAlertRepository) {
        this.fraudAlertRepository = fraudAlertRepository;
    }

    @Override
    public List<FraudAlertResponse> getAlerts(String userEmail, String statusFilter) {
        List<FraudAlert> alerts;

        // "All" (or no filter) returns everything; otherwise filter by
        // status, matching the All/Pending/Reviewed/Blocked tabs.
        if (statusFilter == null || statusFilter.equalsIgnoreCase("ALL")) {
            alerts = fraudAlertRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        } else {
            FraudAlert.AlertStatus status = FraudAlert.AlertStatus.valueOf(statusFilter.toUpperCase());
            alerts = fraudAlertRepository.findByUserEmailAndStatusOrderByCreatedAtDesc(userEmail, status);
        }

        return alerts.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public FraudAlertResponse updateStatus(String userEmail, Long alertId, String status) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));

        if (!alert.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedException("You do not have access to this alert");
        }

        alert.setStatus(FraudAlert.AlertStatus.valueOf(status.toUpperCase()));
        alert = fraudAlertRepository.save(alert);
        return toResponse(alert);
    }

    private FraudAlertResponse toResponse(FraudAlert alert) {
        return new FraudAlertResponse(
                alert.getId(),
                alert.getType().name(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getStatus().name(),
                alert.getCreatedAt());
    }
}