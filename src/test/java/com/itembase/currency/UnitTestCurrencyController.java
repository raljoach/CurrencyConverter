package com.itembase.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    void testConvert() throws JsonProcessingException {
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
