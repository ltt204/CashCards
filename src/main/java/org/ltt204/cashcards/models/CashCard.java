package org.ltt204.cashcards.models;


import org.springframework.data.annotation.Id;

public record CashCard(
        @Id long id,
        double amount
) {}
