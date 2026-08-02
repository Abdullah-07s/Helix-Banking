// Backs the "Transfer Successful!" confirmation card: amount, recipient,
// masked recipient account, and timestamp.

package com.helix.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransferResponse {

    private Long transactionId;
    private BigDecimal amount;
    private String recipientAccountNumberMasked;
    private String status;
    private Instant timestamp;

    public TransferResponse(Long transactionId, BigDecimal amount, String recipientAccountNumberMasked,
            String status, Instant timestamp) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.recipientAccountNumberMasked = recipientAccountNumberMasked;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRecipientAccountNumberMasked() {
        return recipientAccountNumberMasked;
    }

    public void setRecipientAccountNumberMasked(String recipientAccountNumberMasked) {
        this.recipientAccountNumberMasked = recipientAccountNumberMasked;
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