// Card issuance and status management, scoped to the authenticated
// owner (JWT subject) throughout - a user can only see/manage their
// own cards.

package com.helix.card.service;

import com.helix.card.dto.CardResponse;
import com.helix.card.dto.CreateCardRequest;
import com.helix.card.entity.Card;
import com.helix.card.repository.CardRepository;
import com.helix.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public List<CardResponse> getCardsForUser(String email) {
        return cardRepository.findByOwnerEmail(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CardResponse createCard(String email, CreateCardRequest request) {
        Card card = new Card();
        card.setOwnerEmail(email);
        card.setAccountId(request.getAccountId());
        card.setType(Card.CardType.valueOf(request.getType().toUpperCase()));
        card.setNetwork(Card.CardNetwork.valueOf(request.getNetwork().toUpperCase()));
        card.setCardNumber(generateCardNumber());
        // Cards issued today expire 4 years out - a reasonable simulated default.
        card.setExpiry(YearMonth.now().plusYears(4));
        card.setStatus(Card.CardStatus.ACTIVE);

        card = cardRepository.save(card);
        return toResponse(card);
    }

    @Override
    @Transactional
    public CardResponse updateStatus(String email, Long cardId, String status) {
        Card card = cardRepository.findByIdAndOwnerEmail(cardId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        card.setStatus(Card.CardStatus.valueOf(status.toUpperCase()));
        card = cardRepository.save(card);
        return toResponse(card);
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private CardResponse toResponse(Card card) {
        String masked = "**** " + card.getCardNumber().substring(card.getCardNumber().length() - 4);
        String label = capitalize(card.getNetwork().name()) + " " + capitalize(card.getType().name());
        return new CardResponse(
                card.getId(),
                card.getAccountId(),
                card.getType().name(),
                card.getNetwork().name(),
                masked,
                card.getExpiry().format(EXPIRY_FORMAT),
                card.getStatus().name());
    }

    private String capitalize(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}