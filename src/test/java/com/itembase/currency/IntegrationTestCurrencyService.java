package com.itembase.currency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
public class IntegrationTestCurrencyService {
    @Autowired
    CurrencyService currencyService;

    private static Boolean useShuffle = false;

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
        TestUtils.setConfig(r, useShuffle, 0);
    }

    @Test
    void testConvert() {
        // arrange input
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 1.125;
        var convertedAmount  = TestUtils.currencyRound(amount*rate1);

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(convertedAmount)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Returns_404() {
        // arrange input
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate2 = 2.620;
        var convertedAmount  = TestUtils.currencyRound(amount*rate2);

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse(404, "NOT FOUND");
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(convertedAmount)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Request_Timeout() {
        // arrange input
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate2 = 2.639;
        var convertedAmount  = TestUtils.currencyRound(amount*rate2);

        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(convertedAmount)
                .verifyComplete();
    }

    @Test
    void testConvert_Both_Api_Request_Timeout() {
        // arrange inputs
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        var errorMessage = "RateError Request timed out";

        // assert
        var action =currencyService.convert(from, to, amount);
        var theException = assertThrows(ApiException.class,()->Mono.when(action).block());
        Assertions.assertNotNull(theException);
        System.out.println("TEST DEBUG:");
        System.out.println("theException.ErrorType: " + theException.getErrorType());
        System.out.println("theException.Message: " + theException.getMessage());
        assertTrue(theException.getErrorType().contains("RateError"));
        assertTrue(theException.getMessage().contains("TimeoutException"));
    }

    @Test
    void testConvert_From_DoesNotExist() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EURX";
        String to = "USD";
        double amount = 40;
        int status = 400;
        var errorMessage ="base does not exist in API";

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse(status, errorMessage);
        TestUtils.addExchangeApiServer1ErrorResponse(status, errorMessage);

        // assert
        var action =currencyService.convert(from, to, amount);
        assertThrows(ApiException.class,()->Mono.when(action).block(),errorMessage);
    }

    @Test
    void testConvert_To_DoesNotExist() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USDX";
        double amount = 40;
        int status = 400;
        var errorMessage ="to does not exist in API";

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse(status, errorMessage);
        TestUtils.addExchangeApiServer2ErrorResponse(status, errorMessage);

        // assert
        var action =currencyService.convert(from, to, amount);
        assertThrows(ApiException.class,()->Mono.when(action).block(),errorMessage);
    }
}
