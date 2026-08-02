// Binds JWT-related configuration (secret key, expiration) from
// application.yml (which in turn reads from .env via ${JWT_SECRET} etc.).
// Centralizing this here means every service configures JWT the same way.

package com.helix.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "helix.jwt")
public class JwtProperties {

    // The signing secret. Must be at least 256 bits (32 chars) for HS256.
    // Sourced from JWT_SECRET in .env - never hardcoded.
    private String secret;

    // Access token validity in milliseconds. Defaults to 1 hour if not set.
    private long expirationMs = 3_600_000;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}