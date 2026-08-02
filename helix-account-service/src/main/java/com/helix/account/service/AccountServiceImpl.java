// Fetches accounts for the currently authenticated user (identified by
// the email extracted from the JWT subject claim by JwtAuthenticationFilter).
// Also exposes adjustBalance() for internal use by the Transaction service.

package com.helix.account.service;

import com.helix.account.dto.AccountResponse;
import com.helix.account.entity.Account;
import com.helix.account.entity.User;
import com.helix.account.repository.AccountRepository;
import com.helix.account.repository.UserRepository;
import com.helix.common.exception.ResourceNotFoundException;
import com.helix.common.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AccountResponse> getAccountsForUser(String email) {
        User user = getUserOrThrow(email);
        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountResponse getAccountById(String email, Long accountId) {
        User user = getUserOrThrow(email);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        if (!account.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have access to this account");
        }

        return toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse adjustBalance(Long accountId, BigDecimal delta) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        BigDecimal newBalance = account.getBalance().add(delta);

        // Prevent overdrafts - the Transaction service should have already
        // validated sufficient funds before calling this, but we double-check
        // here as a safety net since this endpoint could be called directly.
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new UnauthorizedException("Insufficient funds for account: " + accountId);
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
        return toResponse(account);
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private AccountResponse toResponse(Account account) {
        String masked = "**** " + account.getAccountNumber().substring(account.getAccountNumber().length() - 4);
        return new AccountResponse(
                account.getId(),
                account.getType().name(),
                account.getLabel(),
                masked,
                account.getBalance());
    }
}