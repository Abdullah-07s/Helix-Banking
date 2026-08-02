// Backs the Transfer Money screen (with confirmation), the Transaction
// History screen, and the Deposit ("Add Money") flow.
//
// toAccountId query param (transfer only): see design note in
// AccountServiceClient - the frontend resolves and passes the
// recipient's account id alongside the human-entered account number.

package com.helix.transaction.controller;

import com.helix.common.dto.ApiResponse;
import com.helix.transaction.dto.DepositRequest;
import com.helix.transaction.dto.TransactionResponse;
import com.helix.transaction.dto.TransferRequest;
import com.helix.transaction.dto.TransferResponse;
import com.helix.transaction.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestParam Long toAccountId,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        String email = authentication.getName();
        String authHeader = httpRequest.getHeader("Authorization"); // forwarded to Feign calls

        TransferResponse response = transactionService.transfer(email, authHeader, request, toAccountId);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransferResponse>> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        String email = authentication.getName();
        String authHeader = httpRequest.getHeader("Authorization");

        TransferResponse response = transactionService.deposit(email, authHeader, request);
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHistory(Authentication authentication) {
        List<TransactionResponse> history = transactionService.getHistory(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}