// Entry point for the API Gateway. All routing configuration lives in
// application.yml (declarative route definitions) - no custom route
// beans needed for this project's straightforward path-based routing.

package com.helix.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}