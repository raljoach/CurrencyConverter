package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.when;

/* Unit Tests /currency/convert api of CurrencyController
   using mock CurrencyService implementation
 */

/* MISSING CURRENCY SERVICE TEST CASES
   Test 1: ZWL exists exchange api 1, but not exchange api 2
   Test 2: ZWL exists exchange api 2, but not exchange api 1
   Test 3: ZWL not in exchange api 1, not in exchange api 2
   Test 4: exchange api 1 a-z ordered list, exchange api 2 random order list
   Test 5: exchange api 2 a-z ordered list, exchange api 1 random order list
 */


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CurrencyController.class)
public class UnitTestCurrencyController {

    @MockBean
    CurrencyService mockCurrencyService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testConvert() {
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        String from = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(from);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(originalAmount);
        conversionResponse.setConverted(convertedAmount);

        // arrange mock
        Mono<Double> convertedAmountMono = Mono.just(convertedAmount);
        when(mockCurrencyService.convert(from,to,originalAmount)).thenReturn(convertedAmountMono);

        // act, assert
        var theResponse =
        webTestClient.post()
                .uri("/currency/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(conversionRequest))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

                var theBody = theResponse.expectBody();

                theBody.jsonPath("$.from").isEqualTo(from)
                        .jsonPath("$.to").isEqualTo(to)
                        .jsonPath("$.amount").isEqualTo(originalAmount)
                        .jsonPath("$.converted").isEqualTo(convertedAmount)
                        .jsonPath("$.errorCode").doesNotExist()
                        .jsonPath("$.message").doesNotExist();
    }

    @Test
    void testConvertReturn400() {
        // arrange input
        String from="";
        String to="";
        double originalAmount=0;
        double convertedAmount=0;
        String errorCode = "UnknownError";
        String message = "some Random error message";

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(from);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(originalAmount);
        conversionResponse.setConverted(convertedAmount);

        // arrange mock
        when(mockCurrencyService.convert(from,to,originalAmount))
                .thenThrow(new NoSuchFieldError(message));

        // act, assert
        var theResponse =
                webTestClient.post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

        var theBody = theResponse.expectBody();

        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode)
                .jsonPath("$.message").isEqualTo(message);

    }

    @Test
    void testConvertFromValid(){
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("    eUR");
        currencyTypeList.add("EUr    ");
        currencyTypeList.add("  EUR  ");
        currencyTypeList.add("  eUr");
        currencyTypeList.add("usd");

        String from = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(from);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(originalAmount);
        conversionResponse.setConverted(convertedAmount);

        // arrange mock
        Mono<Double> convertedAmountMono = Mono.just(convertedAmount);
        when(mockCurrencyService.convert(from,to,originalAmount)).thenReturn(convertedAmountMono);

        // act, assert
        var theResponse =
                webTestClient.post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

        var theBody = theResponse.expectBody();

        theBody.jsonPath("$.from").isEqualTo(from)
                .jsonPath("$.to").isEqualTo(to)
                .jsonPath("$.amount").isEqualTo(originalAmount)
                .jsonPath("$.converted").isEqualTo(convertedAmount)
                .jsonPath("$.errorCode").doesNotExist()
                .jsonPath("$.message").doesNotExist();
    }

    @Test
    void testConvertFromNotFound() {
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        List<String> notInList = new ArrayList<>();
        notInList.add("ZWL");
        notInList.add("ZMK");
        notInList.add("YER");

        String from = notInList.get(ThreadLocalRandom.current().nextInt(notInList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;
        String errorCode = "NotFound";

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ErrorResponse errorResponse = new ErrorResponse();
        String message ="'from' value not found";
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);

        // arrange mock
        when(mockCurrencyService.convert(from,to,originalAmount))
                .thenThrow(new CurrencyException(errorCode,message));

        // act, assert
        var theResponse =
                webTestClient.post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

        var theBody = theResponse.expectBody();

        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode)
                .jsonPath("$.message").isEqualTo(message);

    }

    @Test
    void testConvertFromBadInput(){
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        List<String> badTypeList = new ArrayList<>();
        badTypeList.add("");
        badTypeList.add(null);
        badTypeList.add(" ");
        badTypeList.add("      ");
        badTypeList.add("ZZZ");
        badTypeList.add("EURR");
        badTypeList.add("123");
        badTypeList.add("@@@");
        badTypeList.add("EUR@");
        badTypeList.add("@EUR");
        badTypeList.add("/EUR/");
        badTypeList.add("\\EUR\\");
        badTypeList.add("<EUR>");
        badTypeList.add("EUR,USD");
        badTypeList.add(" EUR,USD");
        badTypeList.add(" EUR,USD ");
        badTypeList.add("EUR,USD ");
        badTypeList.add("EUR+USD");
        badTypeList.add("EUR-USD");
        badTypeList.add("EUR/USD");
        badTypeList.add("EUR*USD");
        badTypeList.add("EURUSD");
        badTypeList.add("EUR=USD");
        badTypeList.add("EUR USD");
        badTypeList.add("EUR,USD,XXX");
        badTypeList.add("XXX,EUR,USD");
        badTypeList.add("EUR,XXX,USD");

        String from = badTypeList.get(ThreadLocalRandom.current().nextInt(badTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ErrorResponse errorResponse = new ErrorResponse();
        String errorCode = "BadInput";
        String message ="'from' value invalid";
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);

        // arrange mock
        when(mockCurrencyService.convert(from,to,originalAmount))
                .thenThrow(new IllegalArgumentException(message));

        // act, assert
        var theResponse =
                webTestClient.post()
                        .uri("/currency/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(conversionRequest))
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);

        var theBody = theResponse.expectBody();

        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode)
                .jsonPath("$.message").isEqualTo(message);

    }
}