package org.ltt204.cashcards.controller;

import org.ltt204.cashcards.dto.CashCardModifyRequestDto;
import org.ltt204.cashcards.model.CashCard;
import org.ltt204.cashcards.repository.CashCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository _cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        _cashCardRepository = cashCardRepository;
    }


    @GetMapping("/{cashCardId}")
    private ResponseEntity<CashCard> getCashCardById(@PathVariable long cashCardId, Principal principal) {
        var cashCard = findCashCard(cashCardId, principal);
        if (cashCard == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cashCard);
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> getCashCards(Pageable pageable, Principal principal) {
        Page<CashCard> page = _cashCardRepository.findByOwner(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ),
                principal.getName()
        );

        return ResponseEntity.ok(page.getContent());
    }


    /**
     * Where did UriComponentsBuilder come from?
     * We were able to add UriComponentsBuilder ucb as a method argument to this POST handler method,
     * and it was automatically passed in.
     * How so? It was injected from our now-familiar friend, Spring's IoC Container.
     * Thanks, Spring Web!
     **/
    @PostMapping
    private ResponseEntity<?> createCashCard(@RequestBody CashCardModifyRequestDto createRequestDto, UriComponentsBuilder ucb, Principal principal) {
        var newCashCard = _cashCardRepository.save(new CashCard(null, createRequestDto.amount(), principal.getName()));

//        return ResponseEntity.created(URI.create("/cashcards/" + newCashCard.id())).build();
        var locationOfNewCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(newCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCard).build();
    }

    @PutMapping("/{cashCardId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable long cashCardId, @RequestBody CashCardModifyRequestDto cashCardModifyRequestDto, Principal principal) {
        var existingCashCard = findCashCard(cashCardId, principal);

        if (existingCashCard == null) {
            return ResponseEntity.notFound().build();
        }

        var updatedCashCard = new CashCard(existingCashCard.id(), cashCardModifyRequestDto.amount(), principal.getName());
        _cashCardRepository.save(updatedCashCard);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cashCardId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable long cashCardId, Principal principal) {
        if (_cashCardRepository.existsCashCardByIdAndOwner(cashCardId, principal.getName())) {
            _cashCardRepository.deleteById(cashCardId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(long cashCardId, Principal principal) {
        return _cashCardRepository.findByIdAndOwner(cashCardId, principal.getName());
    }
}
