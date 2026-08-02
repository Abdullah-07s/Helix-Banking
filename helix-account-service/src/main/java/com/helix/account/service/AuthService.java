package com.helix.account.service;

import com.helix.account.dto.AuthResponse;
import com.helix.account.dto.LoginRequest;
import com.helix.account.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}