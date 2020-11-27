package com.itembase.currency;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* Functional Tests of /currency/convert api of CurrencyController
   using real Exchange API Web Servers implementation
 */

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CurrencyController.class)
@Import(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
public class End2EndTestCurrencyController {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void testConvert() {
        // arrange input
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();
        double originalAmount = TestUtils.getRandomAmount();

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // act
        var theResponse =
                webTestClient.mutate()
                        .responseTimeout(Duration.ofMillis(50000)).build().post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

        var theBody = theResponse.expectBody();

        //assert
        theBody.jsonPath("$.from").isEqualTo(from)
                .jsonPath("$.to").isEqualTo(to)
                .jsonPath("$.amount").isEqualTo(originalAmount)
                .jsonPath("$.converted").isNotEmpty()//.isEqualTo(convertedAmount)
                .jsonPath("$.converted").isNumber()
                .jsonPath("$.errorCode").doesNotExist()
                .jsonPath("$.message").doesNotExist();
    }

    @Test
    void testConvert_From_DoesNotExist() {
        // arrange input
        String from = "EURX";
        String to = TestUtils.getRandomCurrency();
        double originalAmount = TestUtils.getRandomAmount();

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // assert
        var theResponse =
                webTestClient.mutate()
                        .responseTimeout(Duration.ofMillis(50000)).build().post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void testConvert_To_DoesNotExist() {
        // arrange input
        String to = "EURX";
        String from = TestUtils.getRandomCurrency();
        double originalAmount = TestUtils.getRandomAmount();

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // assert
        var theResponse =
                webTestClient.mutate()
                        .responseTimeout(Duration.ofMillis(50000)).build().post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void testConvert_Amount_BadInput() {
        // arrange input
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();
        double originalAmount = -40;

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // assert
        var theResponse =
                webTestClient.mutate()
                        .responseTimeout(Duration.ofMillis(50000)).build().post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
