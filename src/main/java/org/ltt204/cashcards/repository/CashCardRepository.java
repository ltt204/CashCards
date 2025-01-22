package org.ltt204.cashcards.repository;

import org.ltt204.cashcards.models.CashCard;
import org.springframework.data.repository.CrudRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
