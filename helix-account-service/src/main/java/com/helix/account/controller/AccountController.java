// Backs the "My Accounts" screen, the Dashboard's account summary, and
// (internal) balance adjustments called by the Transaction service.

package com.helix.account.controller;

import com.helix.account.dto.AccountBalanceUpdateRequest;
import com.helix.account.dto.AccountResponse;
import com.helix.account.service.AccountService;
import com.helix.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(Authentication authentication) {
        String email = authentication.getName();
        List<AccountResponse> accounts = accountService.getAccountsForUser(email);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        String email = authentication.getName();
        AccountResponse account = accountService.getAccountById(email, accountId);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    // Internal endpoint used by the Transaction service (via Feign) to
    // debit/credit an account during a transfer. Still requires a valid
    // JWT (any authenticated caller) but doesn't check "ownership" since
    // the caller is a trusted downstream service, not the account owner
    // directly - the Transaction service is responsible for validating
    // the sender legitimately owns the source account before calling this.
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<AccountResponse>> adjustBalance(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountBalanceUpdateRequest request) {
        AccountResponse updated = accountService.adjustBalance(accountId, request.getDelta());
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}