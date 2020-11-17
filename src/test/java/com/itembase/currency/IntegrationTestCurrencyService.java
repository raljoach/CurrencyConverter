package com.itembase.currency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        TestUtils.setConfig(r, useShuffle);
    }

    /* UT: CurrencyService.getRate
     * T1:  both clients available => rate returned
     * T2:  client 1 available, client 2 unavailable   => UnavailableApiError
     * T3:  client 1 unavailable, client 2 available   => rate returned
     * T4:  client 1 timeout, client 2 available       => rate returned
     * T5:  client 1 timeout, client 2 unavailable     => UnavailableApiError
     * T6:  client 1 timeout, client 2 timeout         => UnavailableApiError
     * T7:  client 1 doesn't have base, client 2 has rate => rate returned
     * T7:  client 1 doesn't have base, client 2 doesn't have base => RateUnavailabeError(Reason: 'B' not found)
     * T8:  client 1 doesn't have base, client 2 doesn't have to   => RateUnavailabeError(Reason: 'T' not found)
     * DUPE(T7) client 1 doesn't have to, client 2 doesn't have base => RateUnavailabeError(Reason: 'T' not found)
     * DUPE(T8) client 1 doesn't have to, client 2 doesn't have to
     * T11: client 1 doesn't have to, client 2 has rate => rate returned
     * T12: client 1 has new rate, client 2 has older rate
     * T13: client 1 has older rate, client 2 has newer rate
     *
     * // IGNORE
     * T14: client1 returns negative rate, client 2 returns positive rate => rate returned [WILL ASSUME RATE IS ALWAYS >0]
     * T15: client1 returns negative rate, client 2 returns negative rate => ApiError('Rates are negative') [NOT SURE]
     * T15: client1 returns rate=0, client 2 returns non-zero rate [NOT SURE: WILL ASSUME RATE IS ALWAYS >0]

     * T15: client1 returns rate=0, client 2 returns non-zero rate [WILL ASSUME RATE IS ALWAYS >0]
     *
     * // DO
     * T18: client1 returns X.Y rate, client 2 returns Z.D rate => return X.Y rate
     * T19: client1 returns X.0 rate
     * T20: client1 returns X.Y rate
     * T21: client1 returns X.YZ rate
     * T22: client1 returns X.YZA rate
     * T22: client1 returns X.YZ94444444 rate
     * T22: client1 returns X.YZ95444444 rate
     * T22: client1 returns X.YZ94999999 rate
     * T22: client1 returns X.YZ44999999 rate
     * T22: client1 returns X.YZ49999999 rate
     * T23: client1 returns BX.YZA rate
     * T24: client1 returns +INF.INF rate
     * T25: client1 returns +INF rate
     * T26: client1 returns -INF rate
     * T27: client returns +INF+1 rate => OverFlowError
     * T28: client returns -INF-1 rate => UnderFlowError
     *
     *
     * T21: client1 return cached value, client1 returns new value after cache expires
     */
    @Test
    void testConvert() {
        // arrange input
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 1.125;

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate1)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Returns_404() {
        // arrange input
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate2 = 2.620;

        // arrange mocks
        TestUtils.addExchangeApiServer1ErrorResponse("404", "NOT FOUND");
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate2)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Request_Timeout() {
        // arrange input
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate2 = 2.639;

        // TODO: ExchangeData with all rates should be returned, not single rate
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate2)
                .verifyComplete();
    }

    @Test
    void testConvert_Both_Api_Request_Timeout() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 4.25;
        Double rate2 = 8.60;
        Double rate0 = 20.06;

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectErrorMessage("Request timed out")
                .verify();
    }

    @Test
    void testConvert_From_BadInput() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EURX";
        String to = "USD";
        double amount = 40;
        Double rate1 = 13.25;
        Double rate2 = 22.60;

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectErrorMessage("base does not exist in API")
                .verify();
    }

    @Test
    void testConvert_To_BadInput() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USDX";
        double amount = 40;
        Double rate1 = 44.25;
        Double rate2 = 56.60;

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectErrorMessage("to does not exist in API")
                .verify();
    }

    @Test
    void testConvert_Amount_BadInput() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = -40;

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectErrorMessage("amount is less than zero")
                .verify();
    }

    @Test
    void testConvert_Rate_BadInput() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = -1000.25;
        Double rate2 = -2000.60;


        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);
        TestUtils.addExchangeApiServer2Response(from, to, rate2);

        // assert
        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectErrorMessage("Rate returned by API is negative")
                .verify();

    }

    // TODO: CurrencyService integration test cases
    /*
         [X] T1: 404, good
         [X] T2: timeout, good
         [X] T3: timeout, timeout
         [X] T4: bad input
         [ ] T5: valid: camelCase, lower, UPPER, padded with spaces
         T9: Bad API1 key
         T10: Bad API2 key
         ------------------------------------------------
         T9: lastUpdate of API is older than API2 -> return API2 (do we care?)
         T10: lastUpdate of API2 is older than API1 -> return API1 (do we care?)
     */


/*   // TODO: CurrencyService unit test cases
    // UNIT TEST
    @Test
    void testConvertBaseUrl1Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenThrow(RuntimeException.class);
        when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList("/latest=<FROM>,<TO>", "/<FROM>"));
        throw new Exception("Not Implemented");
    }

    @Test
    void testConvertBaseUrl2Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenReturn(Arrays.asList("https://api1", "https://api2"));
        when(mockApiConfig.getRateUrls()).thenThrow(RuntimeException.class);
        throw new Exception("Not Implemented");
    }

    @Test
    void testConvertApi1Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenReturn(Arrays.asList("https://api1", "https://api2"));
        when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList("/latest=<FROM>,<TO>", "/<FROM>"));
        throw new Exception("Not Implemented");
    }

 */
}
