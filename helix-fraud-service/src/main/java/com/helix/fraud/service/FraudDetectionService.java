package com.helix.fraud.service;

import com.helix.fraud.dto.TransactionEvent;

public interface FraudDetectionService {

    // Evaluates a transaction event against fraud rules and creates
    // alerts (+ publishes notifications) for any that fire.
    void evaluate(TransactionEvent event);
}