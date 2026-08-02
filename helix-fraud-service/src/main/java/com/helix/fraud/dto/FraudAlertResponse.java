// Backs each row on the Alerts & Fraud screen.

package com.helix.fraud.dto;

import java.time.Instant;

public class FraudAlertResponse {

    private Long id;
    private String type;
    private String title;
    private String description;
    private String status;
    private Instant date;

    public FraudAlertResponse(Long id, String type, String title, String description,
            String status, Instant date) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}