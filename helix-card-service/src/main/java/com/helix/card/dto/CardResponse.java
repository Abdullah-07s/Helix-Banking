// Backs the card display on the Accounts screen: masked number,
// network/type label (e.g. "Visa Platinum"), expiry, status badge.

package com.helix.card.dto;

public class CardResponse {

    private Long id;
    private Long accountId;
    private String type;
    private String network;
    private String cardNumberMasked;
    private String expiry; // formatted as MM/YY
    private String status;

    public CardResponse(Long id, Long accountId, String type, String network,
            String cardNumberMasked, String expiry, String status) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.network = network;
        this.cardNumberMasked = cardNumberMasked;
        this.expiry = expiry;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}