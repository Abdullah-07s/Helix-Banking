// Service-specific security wiring. Extends the shared SecurityConfigSupport
// from helix-common, declaring which endpoints are public: register and
// login must be reachable without a token (you don't have one yet!).
// Everything else (accounts, profile) requires a valid JWT.

package com.helix.account.config;

import com.helix.common.security.JwtTokenProvider;
import com.helix.common.security.SecurityConfigSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends SecurityConfigSupport {

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        return buildFilterChain(http,
                "/api/auth/register",
                "/api/auth/login");
    }

    // BCrypt is the industry-standard adaptive hash for password storage.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}