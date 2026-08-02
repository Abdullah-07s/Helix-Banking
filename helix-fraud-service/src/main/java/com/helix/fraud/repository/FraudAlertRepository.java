package com.helix.fraud.repository;

import com.helix.fraud.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    // Backs the "All Alerts" tab - most recent first.
    List<FraudAlert> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // Backs the Pending/Reviewed/Blocked filter tabs.
    List<FraudAlert> findByUserEmailAndStatusOrderByCreatedAtDesc(
            String userEmail, FraudAlert.AlertStatus status);
}