// Internal-use request for adjusting an account's balance by a signed
// delta (positive = credit, negative = debit). Called only by the
// Transaction service via Feign during a transfer - not exposed to
// the frontend directly.

package com.helix.account.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountBalanceUpdateRequest {

    @NotNull(message = "Delta amount is required")
    private BigDecimal delta; // positive to credit, negative to debit

    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }
}