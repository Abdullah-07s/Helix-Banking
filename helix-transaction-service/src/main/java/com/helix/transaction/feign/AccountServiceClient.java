// Feign client for synchronous calls to the Account service: looking up
// an account by its account number (to resolve the transfer recipient)
// and adjusting balances (debit sender / credit recipient).
//
// The incoming request's JWT is forwarded via the Authorization header
// (see FeignAuthInterceptor pattern below) since the Account service's
// SecurityConfig requires a valid token on these endpoints.

package com.helix.transaction.feign;

import com.helix.transaction.dto.feign.AccountFeignResponse;
import com.helix.transaction.dto.feign.DebitCreditRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "helix-account-service", url = "${helix.services.account-url}")
public interface AccountServiceClient {

        @GetMapping("/api/accounts/{accountId}")
        AccountFeignResponse getAccountById(@PathVariable("accountId") Long accountId,
                        @RequestHeader("Authorization") String authHeader);

        @PutMapping("/api/accounts/{accountId}/balance")
        AccountFeignResponse adjustBalance(@PathVariable("accountId") Long accountId,
                        @RequestBody DebitCreditRequest request,
                        @RequestHeader("Authorization") String authHeader);
}