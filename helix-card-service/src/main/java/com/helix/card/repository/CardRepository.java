package com.helix.card.repository;

import com.helix.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByOwnerEmail(String ownerEmail);

    Optional<Card> findByIdAndOwnerEmail(Long id, String ownerEmail);
}