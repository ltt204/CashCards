package org.ltt204.cashcards.controller;

import org.ltt204.cashcards.models.CashCard;
import org.ltt204.cashcards.repository.CashCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository _cashCardRepository;

    @Autowired
    public CashCardController(CashCardRepository cashCardRepository) {
        _cashCardRepository = cashCardRepository;
    }


    @GetMapping("/{cashCardId}")
    private ResponseEntity<CashCard> getCashCardById(@PathVariable long cashCardId) {
        Optional<CashCard> cashCardOptional = _cashCardRepository.findById(cashCardId);

        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
