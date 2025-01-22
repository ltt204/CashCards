package org.ltt204.cashcards;

import org.junit.jupiter.api.Test;
import org.ltt204.cashcards.models.CashCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Test
    void cashCardSerializationTest() throws IOException {
        var cashCardJson = json.write(new CashCard(99L, 123.45));

        assertThat(cashCardJson).isStrictlyEqualToJson("expected.json");
        assertThat(cashCardJson).hasJsonPathNumberValue("@.id");
        assertThat(cashCardJson).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(cashCardJson).hasJsonPathNumberValue("@.amount");
        assertThat(cashCardJson).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 99,
                  "amount": 123.45
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }
}
