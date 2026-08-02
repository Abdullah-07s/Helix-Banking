package com.helix.transaction.repository;

import com.helix.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Transaction history for a user, most recent first - matches the
    // Transaction History screen's default sort order.
    List<Transaction> findByInitiatedByEmailOrderByCreatedAtDesc(String email);
}