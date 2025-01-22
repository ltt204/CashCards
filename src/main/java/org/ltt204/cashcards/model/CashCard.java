package org.ltt204.cashcards.model;


import org.springframework.data.annotation.Id;

public record CashCard(
        @Id Long id,
        double amount,
        String owner
) {}
