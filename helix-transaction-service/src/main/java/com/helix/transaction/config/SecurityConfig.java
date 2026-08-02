// All Transaction endpoints require authentication - there are no public
// endpoints in this service (unlike Account service's register/login).

package com.helix.transaction.config;

import com.helix.common.security.JwtTokenProvider;
import com.helix.common.security.SecurityConfigSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends SecurityConfigSupport {

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        super(jwtTokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // No public patterns - every endpoint requires a valid JWT.
        return buildFilterChain(http);
    }
}