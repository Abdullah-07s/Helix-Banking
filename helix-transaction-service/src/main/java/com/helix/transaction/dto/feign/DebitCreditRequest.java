// Request body sent via Feign to Account service's balance-adjustment
// endpoint. Mirrors AccountBalanceUpdateRequest in the Account service.

package com.helix.transaction.dto.feign;

import java.math.BigDecimal;

public class DebitCreditRequest {

    private BigDecimal delta;

    public DebitCreditRequest() {
    }

    public DebitCreditRequest(BigDecimal delta) {
        this.delta = delta;
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }
}