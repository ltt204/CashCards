package org.ltt204.cashcards.repository;

import org.ltt204.cashcards.model.CashCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    CashCard findByIdAndOwner(Long id, String owner);

    Page<CashCard> findByOwner(Pageable pageable, String owner);

    boolean existsCashCardByIdAndOwner(Long id, String owner);
}
