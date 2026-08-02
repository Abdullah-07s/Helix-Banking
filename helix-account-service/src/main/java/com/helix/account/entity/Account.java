// A bank account (checking/savings/credit) belonging to a user.
// Matches the "My Accounts" screen: type, masked number, balance.
// Note: "Visa Platinum" credit-card-style accounts are still modeled
// here as an Account (type=CREDIT) for the accounts list; the separate
// Card service (Phase 5) manages card-specific details (card number,
// expiry, freeze/block) referencing this account by id.

package com.helix.account.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK to User.id - intentionally a plain Long, not a JPA @ManyToOne,
    // since in a real microservice split we avoid entity relationships
    // that assume shared DB access. Same service here, but kept flat
    // for consistency with how other services reference this data.
    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;

    // Full account number stored server-side; only masked version
    // (e.g. **** 1234) is ever returned in API responses.
    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length = 50)
    private String label; // e.g. "Primary Checking", "Savings Account"

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum AccountType {
        CHECKING, SAVINGS, CREDIT
    }

    // --- Getters and setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}