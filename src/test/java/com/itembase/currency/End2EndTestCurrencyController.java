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

    // TODO: End to End Currency controller test cases
    /*
        T1: API1 available
        T2: API2 available
        T3: bad input
        T4: Bad API2 key
     */


    private static List<String> currencyTypeList = new ArrayList<>();

    @BeforeAll
    static void setup() {
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");
        currencyTypeList.add("AED");
        currencyTypeList.add("ARS");
        currencyTypeList.add("AUD");
        currencyTypeList.add("BGN");
        currencyTypeList.add("BRL");
        currencyTypeList.add("BSD");
        currencyTypeList.add("CAD");
        currencyTypeList.add("CHF");
        currencyTypeList.add("CLP");
        currencyTypeList.add("CNY");
        currencyTypeList.add("COP");
        currencyTypeList.add("CZK");
        currencyTypeList.add("DKK");
        currencyTypeList.add("DOP");
        currencyTypeList.add("EGP");
        currencyTypeList.add("FJD");
        currencyTypeList.add("GBP");
        currencyTypeList.add("GTQ");
        currencyTypeList.add("HKD");
        currencyTypeList.add("HRK");
        currencyTypeList.add("HUF");
        currencyTypeList.add("IDR");
        currencyTypeList.add("ILS");
        currencyTypeList.add("INR");
        currencyTypeList.add("ISK");
        currencyTypeList.add("JPY");
        currencyTypeList.add("KRW");
        currencyTypeList.add("KZT");
        currencyTypeList.add("MVR");
        currencyTypeList.add("MXN");
        currencyTypeList.add("MYR");
        currencyTypeList.add("NOK");
        currencyTypeList.add("NZD");
        currencyTypeList.add("PAB");
        currencyTypeList.add("PEN");
        currencyTypeList.add("PHP");
        currencyTypeList.add("PKR");
        currencyTypeList.add("PLN");
        currencyTypeList.add("PYG");
        currencyTypeList.add("RON");
        currencyTypeList.add("RUB");
        currencyTypeList.add("SAR");
        currencyTypeList.add("SEK");
        currencyTypeList.add("SGD");
        currencyTypeList.add("THB");
        currencyTypeList.add("TRY");
        currencyTypeList.add("TWD");
        currencyTypeList.add("UAH");
        currencyTypeList.add("UYU");
        currencyTypeList.add("ZAR");
    }
/*
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        TestUtils.setShuffle(r, false);
    }
*/

    @Test
    void testConvert() {
        // arrange input
        //String from = "GTQ";
        String from = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 1_000_000_001);

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
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 1_000_000_001);

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
        String from = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 1_000_000_001);

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
        String from = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
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
