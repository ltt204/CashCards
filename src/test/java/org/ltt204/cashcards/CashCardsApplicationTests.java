package org.ltt204.cashcards;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.h2.util.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.ltt204.cashcards.dto.CashCardCreateRequestDto;
import org.ltt204.cashcards.model.CashCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CashCardsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardsApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(CashCardsApplicationTests.class);
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    /**
     * Note for DirtiesContext:
     * Why we need this?
     * For instance if I do not use this. When run the test will be failed!
     * (This was said by Spring Academy, Mine still passed : D)
     * Because we have a Post unit test right below, then the cashCardCount will not be 3 as we expected, but 4 instead
     * So it's likely clearing the before invoking another unittest for better result.
     **/
    @Test
    @DirtiesContext
    void shouldReturnCashCardUriWhenDataIsCreated() {
        double amountForCreateCashCard = 999.99;

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/cashcards",
                new CashCardCreateRequestDto(amountForCreateCashCard),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        HttpHeaders headers = response.getHeaders();
        System.out.println((headers.getLocation()));

        ResponseEntity<String> responseFromLocationInHeader = restTemplate.getForEntity(headers.getLocation(), String.class);

        DocumentContext documentContext = JsonPath.parse(responseFromLocationInHeader.getBody());
        assertThat(responseFromLocationInHeader.getStatusCode()).isEqualTo(HttpStatus.OK);

        Number id = documentContext.read("$.id");
        Number amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(amountForCreateCashCard);
    }

    @Test
    void shouldReturnAllCashCardWhenListIsRequired() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<CashCard > page = documentContext.read("$.[*]");
        assertThat(page.size()).isEqualTo(2);
    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=2&sort=amount,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<CashCard> page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(2);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.01);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<CashCard> page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        List<Double> amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.0, 123.45, 150.01);
    }
}
