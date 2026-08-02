// Mirrors the Account service's ApiResponse<AccountResponse> shape for
// Feign deserialization. Kept separate from the Account service's own
// DTOs since microservices should not share entity/DTO classes across
// module boundaries (that would create tight compile-time coupling).

package com.helix.transaction.dto.feign;

import java.math.BigDecimal;

public class AccountFeignResponse {

    private boolean success;
    private String message;
    private AccountData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AccountData getData() {
        return data;
    }

    public void setData(AccountData data) {
        this.data = data;
    }

    public static class AccountData {
        private Long id;
        private String type;
        private String label;
        private String accountNumberMasked;
        private BigDecimal balance;

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
}