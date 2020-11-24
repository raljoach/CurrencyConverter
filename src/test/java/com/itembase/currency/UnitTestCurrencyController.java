package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/* Unit Tests /currency/convert api of CurrencyController
   using mock CurrencyService implementation
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

        String from = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = TestUtils.random().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = TestUtils.random().nextDouble(0, 101);
        }
        convertedAmount = TestUtils.currencyRound(convertedAmount);
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
        String errorCode = "BadInput";
        String errorMessage = "from is empty";

        var conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // arrange mock
        //when(mockCurrencyService.convert(from,to,originalAmount))
        //        .thenThrow(new NoSuchFieldError(errorMessage));

        // assert
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
                .jsonPath("$.errorCode").isEqualTo(errorCode);

        theResponse.expectBody(ErrorResponse.class).consumeWith(
                response->{
                    var err = response.getResponseBody();
                    assertEquals(errorCode, err.getErrorCode());
                    assertTrue(err.getMessage().contains(errorMessage));
                });

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

        String from = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = TestUtils.random().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = TestUtils.random().nextDouble(0, 101);
        }

        var conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

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

        String from = notInList.get(TestUtils.random().nextInt(notInList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = TestUtils.random().nextDouble(0, 101);
        String errorCode = "NotFound";
        var errorMessage = "'from' value not found";
        var conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // arrange mock
        when(mockCurrencyService.convert(from,to,originalAmount))
                .thenThrow(new CurrencyException(errorCode,errorMessage));

        // act
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

        // assert
        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode)
                .jsonPath("$.message").isEqualTo(errorMessage);

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

        String from = badTypeList.get(TestUtils.random().nextInt(badTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = TestUtils.random().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = TestUtils.random().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ErrorResponse errorResponse = new ErrorResponse();
        String errorCode = "BadInput";
        String errorMessage ="'from' value invalid";
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(errorMessage);

        // arrange mock
        when(mockCurrencyService.convert(from,to,originalAmount))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // act
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

        debug(theResponse);

        // assert
        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode);

        theResponse.expectBody(ErrorResponse.class)
                .consumeWith(
                response->{
                    var err = response.getResponseBody();
                    assertEquals(errorCode, err.getErrorCode());
                    assertTrue(err.getMessage().contains(errorMessage));
                });

    }

    private void debug(WebTestClient.ResponseSpec theResponse) {
        theResponse.expectBody(String.class).consumeWith(
                response->{
                    var body = response.getResponseBody();
                    System.out.println(body);
                });
    }

    @Test
    void testConvert_Amount_BadInput() {
        // arrange input
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");
        String from = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = -40;
        var errorCode="BadInput";
        var errorMessage  = "amount is less than zero";
        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // assert
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

        // assert
        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo(errorCode);

        theResponse.expectBody(ErrorResponse.class).consumeWith(
                response->{
                    var err = response.getResponseBody();
                    assertEquals(errorCode, err.getErrorCode());
                    assertTrue(err.getMessage().contains(errorMessage));
                });

    }
}
