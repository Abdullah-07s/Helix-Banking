package com.helix.fraud.service;

import com.helix.fraud.dto.FraudAlertResponse;

import java.util.List;

public interface FraudAlertService {

    List<FraudAlertResponse> getAlerts(String userEmail, String statusFilter);

    FraudAlertResponse updateStatus(String userEmail, Long alertId, String status);
}