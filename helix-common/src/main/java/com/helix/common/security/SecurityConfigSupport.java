// Base class each service's own @Configuration class can extend to get
// consistent security wiring: stateless sessions, CSRF disabled (pure
// REST API, no cookies/forms), JWT filter registered, and a pluggable
// list of public (unauthenticated) endpoint patterns per-service
// (e.g. Account service needs /api/auth/login and /api/auth/register public).

package com.helix.common.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public abstract class SecurityConfigSupport {

    protected final JwtTokenProvider jwtTokenProvider;

    protected SecurityConfigSupport(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Subclasses call this from their @Bean SecurityFilterChain method,
     * passing the specific public (permit-all) URL patterns for that service.
     */
    protected SecurityFilterChain buildFilterChain(HttpSecurity http, String... publicPatterns) throws Exception {
        http
                // Stateless REST API - no CSRF tokens, no session cookies.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                        .requestMatchers(publicPatterns).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}