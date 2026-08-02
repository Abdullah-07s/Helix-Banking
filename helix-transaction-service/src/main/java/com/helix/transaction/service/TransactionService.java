package com.helix.transaction.service;

import com.helix.transaction.dto.DepositRequest;
import com.helix.transaction.dto.TransactionResponse;
import com.helix.transaction.dto.TransferRequest;
import com.helix.transaction.dto.TransferResponse;

import java.util.List;

public interface TransactionService {

    TransferResponse transfer(String initiatorEmail, String authHeader, TransferRequest request, Long toAccountId);

    TransferResponse deposit(String initiatorEmail, String authHeader, DepositRequest request);

    List<TransactionResponse> getHistory(String email);
}