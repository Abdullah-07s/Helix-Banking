// Handles registration (creates User + a default Primary Checking account)
// and login (verifies password, issues JWT).

package com.helix.account.service;

import com.helix.account.dto.AuthResponse;
import com.helix.account.dto.LoginRequest;
import com.helix.account.dto.RegisterRequest;
import com.helix.account.entity.Account;
import com.helix.account.entity.User;
import com.helix.account.repository.AccountRepository;
import com.helix.account.repository.UserRepository;
import com.helix.common.exception.UnauthorizedException;
import com.helix.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UnauthorizedException("Password and confirm password do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("An account with this email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        // Every new user gets one default Primary Checking account,
        // matching the "welcome" state implied by the dashboard mockup.
        Account defaultAccount = new Account();
        defaultAccount.setUserId(user.getId());
        defaultAccount.setType(Account.AccountType.CHECKING);
        defaultAccount.setLabel("Primary Checking");
        defaultAccount.setAccountNumber(generateAccountNumber());
        defaultAccount.setBalance(BigDecimal.ZERO);
        accountRepository.save(defaultAccount);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getFullName(), user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getFullName(), user.getEmail());
    }

    // Generates a random 10-digit account number. Collisions are astronomically
    // unlikely at this project's scale; a uniqueness retry loop would be
    // over-engineering for a learning project.
    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}