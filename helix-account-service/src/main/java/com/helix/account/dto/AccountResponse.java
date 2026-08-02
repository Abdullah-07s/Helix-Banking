// Backs the "My Accounts" list and the Dashboard's account summary.
// accountNumberMasked shows only last 4 digits (e.g. "**** 1234"),
// matching the reference screens exactly.

package com.helix.account.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String type;
    private String label;
    private String accountNumberMasked;
    private BigDecimal balance;

    public AccountResponse(Long id, String type, String label, String accountNumberMasked, BigDecimal balance) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.accountNumberMasked = accountNumberMasked;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}