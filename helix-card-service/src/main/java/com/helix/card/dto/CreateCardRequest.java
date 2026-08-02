// Payload for issuing a new card against an existing account - backs
// the "+ Add Account" / add-card flow implied by the Accounts screen.

package com.helix.card.dto;

import jakarta.validation.constraints.NotNull;

public class CreateCardRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Card type is required")
    private String type; // DEBIT or CREDIT

    @NotNull(message = "Card network is required")
    private String network; // VISA or MASTERCARD

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}