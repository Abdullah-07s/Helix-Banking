// Orchestrates transfers and deposits.
//
// TRANSFER: validates source account ownership and balance (via Feign
// call to Account service), debits sender, credits recipient, records
// the Transaction row, and publishes a Kafka event.
//
// DEPOSIT: credits the caller's own account directly - no source
// account, since funds originate outside the system. Still records a
// Transaction row and publishes a Kafka event so large deposits are
// evaluated by fraud detection the same way large transfers are.
//
// NOTE ON toAccountId (transfer only): as documented in
// AccountServiceClient, this learning-project version accepts the
// recipient account id directly from the caller (frontend resolves
// this) rather than adding a lookup-by-account-number endpoint to the
// Account service, keeping this phase's cross-service surface area
// minimal per project scope rules.

package com.helix.transaction.service;

import com.helix.common.exception.UnauthorizedException;
import com.helix.transaction.dto.DepositRequest;
import com.helix.transaction.dto.TransactionEvent;
import com.helix.transaction.dto.TransactionResponse;
import com.helix.transaction.dto.TransferRequest;
import com.helix.transaction.dto.TransferResponse;
import com.helix.transaction.dto.feign.AccountFeignResponse;
import com.helix.transaction.dto.feign.DebitCreditRequest;
import com.helix.transaction.entity.Transaction;
import com.helix.transaction.feign.AccountServiceClient;
import com.helix.transaction.kafka.TransactionEventProducer;
import com.helix.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

        private final TransactionRepository transactionRepository;
        private final AccountServiceClient accountServiceClient;
        private final TransactionEventProducer eventProducer;

        @Autowired
        public TransactionServiceImpl(TransactionRepository transactionRepository,
                        AccountServiceClient accountServiceClient,
                        TransactionEventProducer eventProducer) {
                this.transactionRepository = transactionRepository;
                this.accountServiceClient = accountServiceClient;
                this.eventProducer = eventProducer;
        }

        @Override
        @Transactional
        public TransferResponse transfer(String initiatorEmail, String authHeader,
                        TransferRequest request, Long toAccountId) {

                // 1. Verify the source account exists and belongs to the caller
                // (getAccountById on the Account service already enforces
                // ownership via the forwarded JWT).
                AccountFeignResponse sourceAccount = accountServiceClient.getAccountById(
                                request.getFromAccountId(), authHeader);

                if (sourceAccount == null || !sourceAccount.isSuccess()) {
                        throw new UnauthorizedException("Unable to verify source account");
                }

                if (sourceAccount.getData().getBalance().compareTo(request.getAmount()) < 0) {
                        throw new UnauthorizedException("Insufficient funds");
                }

                // 2. Debit sender, credit recipient - two Feign calls.
                // NOTE: in a production system this would use a saga/outbox
                // pattern for distributed-transaction safety; for this
                // learning project's scope, sequential calls are acceptable.
                accountServiceClient.adjustBalance(
                                request.getFromAccountId(),
                                new DebitCreditRequest(request.getAmount().negate()),
                                authHeader);
                accountServiceClient.adjustBalance(
                                toAccountId,
                                new DebitCreditRequest(request.getAmount()),
                                authHeader);

                // 3. Record the transaction.
                Transaction txn = new Transaction();
                txn.setType(Transaction.TransactionType.TRANSFER);
                txn.setInitiatedByEmail(initiatorEmail);
                txn.setFromAccountId(request.getFromAccountId());
                txn.setToAccountId(toAccountId);
                txn.setRecipientAccountNumber(request.getRecipientAccountNumber());
                txn.setAmount(request.getAmount());
                txn.setNote(request.getNote());
                txn.setStatus(Transaction.TransactionStatus.SUCCESSFUL);
                txn = transactionRepository.save(txn);

                // 4. Publish Kafka event for Fraud-Detection to consume.
                eventProducer.publish(new TransactionEvent(
                                txn.getId(),
                                initiatorEmail,
                                txn.getFromAccountId(),
                                txn.getToAccountId(),
                                txn.getAmount(),
                                txn.getStatus().name(),
                                txn.getCreatedAt()));

                String maskedRecipient = "**** " + request.getRecipientAccountNumber()
                                .substring(Math.max(0, request.getRecipientAccountNumber().length() - 4));

                return new TransferResponse(
                                txn.getId(),
                                txn.getAmount(),
                                maskedRecipient,
                                txn.getStatus().name(),
                                txn.getCreatedAt());
        }

        @Override
        @Transactional
        public TransferResponse deposit(String initiatorEmail, String authHeader, DepositRequest request) {

                // Verify the account exists and belongs to the caller (ownership
                // check happens inside Account service's getAccountById via the
                // forwarded JWT - same pattern as transfer()).
                AccountFeignResponse account = accountServiceClient.getAccountById(
                                request.getAccountId(), authHeader);

                if (account == null || !account.isSuccess()) {
                        throw new UnauthorizedException("Unable to verify account");
                }

                // Credit the account.
                accountServiceClient.adjustBalance(
                                request.getAccountId(),
                                new DebitCreditRequest(request.getAmount()),
                                authHeader);

                // Record the transaction. No fromAccountId (money originates
                // outside the system) and toAccountId is the deposited-into account.
                Transaction txn = new Transaction();
                txn.setType(Transaction.TransactionType.DEPOSIT);
                txn.setInitiatedByEmail(initiatorEmail);
                txn.setFromAccountId(null);
                txn.setToAccountId(request.getAccountId());
                txn.setRecipientAccountNumber(account.getData().getAccountNumberMasked());
                txn.setAmount(request.getAmount());
                txn.setNote(request.getNote());
                txn.setStatus(Transaction.TransactionStatus.SUCCESSFUL);
                txn = transactionRepository.save(txn);

                // Publish a Kafka event too, for consistency (fraud rules still
                // apply the same way to a large deposit as a large transfer).
                eventProducer.publish(new TransactionEvent(
                                txn.getId(),
                                initiatorEmail,
                                txn.getFromAccountId(),
                                txn.getToAccountId(),
                                txn.getAmount(),
                                txn.getStatus().name(),
                                txn.getCreatedAt()));

                return new TransferResponse(
                                txn.getId(),
                                txn.getAmount(),
                                account.getData().getAccountNumberMasked(),
                                txn.getStatus().name(),
                                txn.getCreatedAt());
        }

        @Override
        public List<TransactionResponse> getHistory(String email) {
                return transactionRepository.findByInitiatedByEmailOrderByCreatedAtDesc(email)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private TransactionResponse toResponse(Transaction txn) {
                String description;
                String type;

                if (txn.getType() == Transaction.TransactionType.DEPOSIT) {
                        description = "Deposit";
                        type = "Credit";
                } else {
                        description = "Transfer to **** " + txn.getRecipientAccountNumber()
                                        .substring(Math.max(0, txn.getRecipientAccountNumber().length() - 4));
                        type = "Debit";
                }

                return new TransactionResponse(
                                txn.getId(),
                                description,
                                type,
                                txn.getAmount(),
                                txn.getStatus().name(),
                                txn.getCreatedAt());
        }
}