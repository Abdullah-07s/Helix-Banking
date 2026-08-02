// Payload for freezing/blocking/reactivating a card - a natural card-
// management action alongside the Accounts/Cards screen.

package com.helix.card.dto;

import jakarta.validation.constraints.NotNull;

public class CardStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private String status; // ACTIVE, FROZEN, BLOCKED

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}