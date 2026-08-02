// Entry point for the Account microservice. scanBasePackages is widened
// to "com.helix" (not just "com.helix.account") so Spring picks up
// GlobalExceptionHandler and other shared beans living in helix-common.

package com.helix.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.helix")
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}