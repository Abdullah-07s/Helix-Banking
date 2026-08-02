// Backs the card entries on the Accounts screen and card management
// actions (issue new card, freeze/block/reactivate).

package com.helix.card.controller;

import com.helix.card.dto.CardResponse;
import com.helix.card.dto.CardStatusUpdateRequest;
import com.helix.card.dto.CreateCardRequest;
import com.helix.card.service.CardService;
import com.helix.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CardResponse>>> getMyCards(Authentication authentication) {
        List<CardResponse> cards = cardService.getCardsForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @Valid @RequestBody CreateCardRequest request,
            Authentication authentication) {
        CardResponse card = cardService.createCard(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Card created successfully", card));
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<ApiResponse<CardResponse>> updateStatus(
            @PathVariable Long cardId,
            @Valid @RequestBody CardStatusUpdateRequest request,
            Authentication authentication) {
        CardResponse card = cardService.updateStatus(authentication.getName(), cardId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Card status updated", card));
    }
}