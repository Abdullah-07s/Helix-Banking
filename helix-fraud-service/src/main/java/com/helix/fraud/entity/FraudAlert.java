// A fraud/security alert record. Matches the Alerts & Fraud screen:
// alert type/title, description, status badge (Pending/Reviewed/Blocked),
// and timestamp.

package com.helix.fraud.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fraud_alerts")
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user this alert concerns (from the transaction event's initiator).
    @Column(nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertType type;

    @Column(nullable = false, length = 255)
    private String title; // e.g. "High Value Transaction"

    @Column(length = 500)
    private String description; // e.g. "$2,500.00 at Online Store"

    // Nullable - not every alert type is transaction-linked (e.g. a future
    // login-based alert wouldn't have this), but high-value alerts do.
    private Long relatedTransactionId;

    private BigDecimal relatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum AlertType {
        HIGH_VALUE_TRANSACTION, MULTIPLE_FAILED_LOGINS, SUSPICIOUS_LOCATION
        // Only HIGH_VALUE_TRANSACTION is actively produced in this phase;
        // the others exist so the schema/API can represent them generically
        // once/if their event sources are added later.
    }

    public enum AlertStatus {
        PENDING, REVIEWED, BLOCKED
    }

    // --- Getters and setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
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

    public Long getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(Long relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public BigDecimal getRelatedAmount() {
        return relatedAmount;
    }

    public void setRelatedAmount(BigDecimal relatedAmount) {
        this.relatedAmount = relatedAmount;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}