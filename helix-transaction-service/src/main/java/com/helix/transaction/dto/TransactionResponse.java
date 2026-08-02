// Backs each row of the Transaction History screen.

package com.helix.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private Long id;
    private String description; // e.g. "Transfer to James Smith" - derived from recipient info
    private String type; // "Debit" or "Credit" relative to the viewer
    private BigDecimal amount;
    private String status;
    private Instant date;

    public TransactionResponse(Long id, String description, String type, BigDecimal amount,
            String status, Instant date) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}