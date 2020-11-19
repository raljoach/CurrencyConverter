package com.itembase.currency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/* Functional Tests of /currency/convert api of CurrencyController
   using mock Exchange API Web Servers implementation
 */

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CurrencyController.class)
@Import(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
//@AutoConfigureWebTestClient(timeout = "36000")
public class IntegrationTestCurrencyController {

    @MockBean
    ExchangeClient mockExchangeClient;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CurrencyService currencyService;

    @BeforeEach
    void testStart() throws IOException {
        TestUtils.startExchangeApiServers();
    }

    @AfterEach
    void testStop() throws IOException {
        TestUtils.stopExchangeApiServers();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        TestUtils.setConfig(r, true, 0);
    }

    @Test
    void testConvert() {
        // arrange input
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        String from = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(TestUtils.random().nextInt(currencyTypeList.size()));
        double originalAmount = TestUtils.random().nextDouble(0, 1_000_000_001);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = TestUtils.random().nextDouble(0, 101);
        }
        convertedAmount  = TestUtils.currencyRound(convertedAmount);
        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // arrange mocks
        Double rate = convertedAmount/originalAmount;
        TestUtils.addExchangeApiServer1Response(from, to, rate);
        TestUtils.addExchangeApiServer2Response(from, to, rate);

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
    void testConvert_From_DoesNotExist() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EURX";
        String to = "USD";
        double amount = 40;
        int status = 400;
        var errorMessage ="400 Bad Request from GET http://localhost:";

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, amount);

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse(status, errorMessage);
        TestUtils.addExchangeApiServer2ErrorResponse(status, errorMessage);

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
                .jsonPath("$.errorCode").isEqualTo("RateError");
                //.jsonPath("$.message").isEqualTo(errorMessage);


        theResponse.expectBody(ErrorResponse.class).consumeWith(
                response->{
                    var err = response.getResponseBody();
                    assertEquals("RateError", err.getErrorCode());
                    assertTrue(err.getMessage().contains(errorMessage));
                });
    }

    @Test
    void testConvert_To_DoesNotExist() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USDX";
        double amount = 40;
        int status = 400;
        var errorMessage ="400 Bad Request from GET http://localhost:";

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, amount);

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse(status, errorMessage);
        TestUtils.addExchangeApiServer2ErrorResponse(status, errorMessage);

        /// assert
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

        var theBody = theResponse.expectBody();

        theBody.jsonPath("$.from").doesNotExist()
                .jsonPath("$.to").doesNotExist()
                .jsonPath("$.amount").doesNotExist()
                .jsonPath("$.converted").doesNotExist()
                .jsonPath("$.errorCode").isEqualTo("RateError");
                //.jsonPath("$.message").isEqualTo(errorMessage);

        theResponse.expectBody(ErrorResponse.class).consumeWith(
                response->{
                    var err = response.getResponseBody();
                    assertEquals("RateError", err.getErrorCode());
                    assertTrue(err.getMessage().contains(errorMessage));
                });
    }
}
