package com.helix.fraud.service;

import com.helix.fraud.dto.AlertNotification;
import com.helix.fraud.dto.TransactionEvent;
import com.helix.fraud.entity.FraudAlert;
import com.helix.fraud.rabbitmq.AlertNotificationProducer;
import com.helix.fraud.repository.FraudAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final AlertNotificationProducer alertNotificationProducer;

    @Value("${helix.fraud.high-value-threshold}")
    private BigDecimal highValueThreshold;

    @Autowired
    public FraudDetectionServiceImpl(FraudAlertRepository fraudAlertRepository,
            AlertNotificationProducer alertNotificationProducer) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.alertNotificationProducer = alertNotificationProducer;
    }

    @Override
    @Transactional
    public void evaluate(TransactionEvent event) {
        if (event.getAmount() != null && event.getAmount().compareTo(highValueThreshold) > 0) {
            FraudAlert alert = new FraudAlert();
            alert.setUserEmail(event.getInitiatedByEmail());
            alert.setType(FraudAlert.AlertType.HIGH_VALUE_TRANSACTION);
            alert.setTitle("High Value Transaction");
            alert.setDescription(String.format("$%,.2f transaction flagged for review", event.getAmount()));
            alert.setRelatedTransactionId(event.getTransactionId());
            alert.setRelatedAmount(event.getAmount());
            alert.setStatus(FraudAlert.AlertStatus.PENDING);

            alert = fraudAlertRepository.save(alert);

            alertNotificationProducer.publish(new AlertNotification(
                    alert.getId(),
                    alert.getUserEmail(),
                    alert.getTitle(),
                    alert.getDescription(),
                    alert.getCreatedAt()));
        }
    }
}