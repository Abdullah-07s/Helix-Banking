package com.helix.account.service;

import com.helix.account.dto.AccountResponse;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<AccountResponse> getAccountsForUser(String email);

    AccountResponse getAccountById(String email, Long accountId);

    AccountResponse adjustBalance(Long accountId, BigDecimal delta);
}