// Mirrors the Transaction service's TransactionEvent payload for Kafka
// deserialization. Kept as a separate class (not shared across module
// boundaries) per microservice independence - each service owns its
// own view of the contract it consumes.

package com.helix.fraud.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class TransactionEvent implements Serializable {

    private Long transactionId;
    private String initiatedByEmail;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String status;
    private Instant timestamp;

    public TransactionEvent() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getInitiatedByEmail() {
        return initiatedByEmail;
    }

    public void setInitiatedByEmail(String initiatedByEmail) {
        this.initiatedByEmail = initiatedByEmail;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}