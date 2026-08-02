// The message payload published to RabbitMQ when a new fraud alert is
// created. In a full system this would be consumed by a notification/
// email service; here it demonstrates the RabbitMQ alert flow per the
// project's required architecture.

package com.helix.fraud.dto;

import java.io.Serializable;
import java.time.Instant;

public class AlertNotification implements Serializable {

    private Long alertId;
    private String userEmail;
    private String title;
    private String description;
    private Instant timestamp;

    public AlertNotification() {
    }

    public AlertNotification(Long alertId, String userEmail, String title,
            String description, Instant timestamp) {
        this.alertId = alertId;
        this.userEmail = userEmail;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}