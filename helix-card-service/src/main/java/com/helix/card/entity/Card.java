// A payment card (debit/credit) linked to an underlying bank Account.
// Matches the "Visa Platinum •••• 5678" style entry on the Accounts
// screen and the card-management portion of the UI.

package com.helix.card.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owning user - identified by email (JWT subject), same flat-reference
    // pattern used by Account entity, avoiding cross-service DB coupling.
    @Column(nullable = false)
    private String ownerEmail;

    // FK-by-value to the underlying Account (from helix_account schema).
    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardNetwork network; // VISA, MASTERCARD - matches "Visa Platinum" label

    @Column(nullable = false, length = 20)
    private String cardNumber; // full number stored server-side, only masked in responses

    @Column(nullable = false)
    private YearMonth expiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum CardType {
        DEBIT, CREDIT
    }

    public enum CardNetwork {
        VISA, MASTERCARD
    }

    public enum CardStatus {
        ACTIVE, FROZEN, BLOCKED
    }

    // --- Getters and setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardNetwork getNetwork() {
        return network;
    }

    public void setNetwork(CardNetwork network) {
        this.network = network;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public YearMonth getExpiry() {
        return expiry;
    }

    public void setExpiry(YearMonth expiry) {
        this.expiry = expiry;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}