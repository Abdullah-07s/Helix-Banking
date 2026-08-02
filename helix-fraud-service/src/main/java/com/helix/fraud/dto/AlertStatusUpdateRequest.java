// Payload for reviewing/blocking an alert from the Alerts screen
// (tapping an alert row presumably lets the user mark it reviewed/blocked).

package com.helix.fraud.dto;

import jakarta.validation.constraints.NotNull;

public class AlertStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private String status; // PENDING, REVIEWED, BLOCKED

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}