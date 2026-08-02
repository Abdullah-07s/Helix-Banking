package com.helix.card.service;

import com.helix.card.dto.CardResponse;
import com.helix.card.dto.CreateCardRequest;

import java.util.List;

public interface CardService {

    List<CardResponse> getCardsForUser(String email);

    CardResponse createCard(String email, CreateCardRequest request);

    CardResponse updateStatus(String email, Long cardId, String status);
}