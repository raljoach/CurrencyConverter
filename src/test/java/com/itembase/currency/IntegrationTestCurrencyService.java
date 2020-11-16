package com.itembase.currency;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
public class IntegrationTestCurrencyService {
    //@MockBean
    //ExchangeClient mockExchangeClient;

    @Autowired
    CurrencyService currencyService;

    private static MockWebServer exchangeApiServer1;
    private static MockWebServer exchangeApiServer2;
    private static Boolean useShuffle = false;

    @BeforeAll
    static void setup() throws IOException {
        //start();
    }

    static void start() throws IOException {
        exchangeApiServer1 = new MockWebServer();
        exchangeApiServer1.start(8181);
        exchangeApiServer2 = new MockWebServer();
        exchangeApiServer2.start(7171);
    }

    @AfterAll
    static void tearDown() throws IOException {
        //stop();
    }

    @BeforeEach
    void testStart() throws IOException {
        start();
    }

    @AfterEach
    void testStop() throws IOException {
        stop();
    }

    static void stop() throws IOException {
        exchangeApiServer1.shutdown();
        exchangeApiServer2.shutdown();
    }
/*
    @AfterEach
    void cleanup() throws InterruptedException {
        try {

            while (exchangeApiServer1.getRequestCount() > 0) {
                exchangeApiServer1.takeRequest(0, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex)
        {}

        try{
        while(exchangeApiServer2.getRequestCount()>0)
        {
            exchangeApiServer2.takeRequest(0, TimeUnit.MILLISECONDS);
        }
        } catch (Exception ex)
        {}
    }*/

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        r.add("useShuffle", () -> useShuffle.toString());
        r.add("exchange.baseUrls[0]", () -> String.format("http://localhost:%s",8181));
                //exchangeApiServer1.getPort()));
        r.add("exchange.baseUrls[1]", () -> String.format("http://localhost:%s",7171));
                //exchangeApiServer2.getPort()));
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
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 1.125;
        Double rate2 = 2.856;
        Double rate0 = 1.05;//0.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(rate1.toString())
                .addHeader("Content-Type", "application/json"));

        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate1)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Returns_404() {
        //doNothing().when(mockApiConfig).shuffle();
        //when(mockApiConfig.getBaseUrls()).thenThrow(RuntimeException.class);
        //when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList("/latest=<FROM>,<TO>", "/<FROM>"));

        String from = "EUR";
        String to = "USD";
        double amount = 40;
        //Double rate1 = 1.325;
        Double rate2 = 2.620;
        //Double rate0 = 0.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        exchangeApiServer1.enqueue(new MockResponse()
                .setStatus("404")
                .setBody("NOT FOUND")
                .addHeader("Content-Type", "application/json"));

        // TODO: ExchangeData with all rates should be returned, not single rate
        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate2)
                .verifyComplete();
    }

    @Test
    void testConvert_Api1_Request_Timeout() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 1.425;
        Double rate2 = 2.639;
        Double rate0 = 0.16;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        // TODO: ExchangeData with all rates should be returned, not single rate
        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectNext(amount*rate2)
                .verifyComplete();
    }

    @Test
    void testConvert_Both_Api_Request_Timeout() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = 40;
        Double rate1 = 4.25;
        Double rate2 = 8.60;
        Double rate0 = 20.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectError()
                .verify();
    }

    @Test
    void testConvert_From_BadInput() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EURX";
        String to = "USD";
        double amount = 40;
        Double rate1 = 13.25;
        Double rate2 = 22.60;
        Double rate0 = 05.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));
        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(rate1.toString())
                .addHeader("Content-Type", "application/json"));

        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectError()
                .verify();
    }

    @Test
    void testConvert_To_BadInput() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USDX";
        double amount = 40;
        Double rate1 = 44.25;
        Double rate2 = 56.60;
        Double rate0 = 99.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(rate1.toString())
                .addHeader("Content-Type", "application/json"));

        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectError()
                .verify();
    }

    @Test
    void testConvert_Amount_BadInput() {
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = -40;
        Double rate1 = 101.25;
        Double rate2 = 102.60;
        Double rate0 = 100.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(rate1.toString())
                .addHeader("Content-Type", "application/json"));

        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectError()
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
        Double rate0 = -896.06;

        // TODO: Not sure this is needed/used [VERIFY if this is used]
        //when(mockExchangeClient.getRate(any(String.class)))
        //        .thenReturn(Mono.just(-5.0));

        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(rate1.toString())
                .addHeader("Content-Type", "application/json"));

        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(rate2.toString())
                .addHeader("Content-Type", "application/json"));


        StepVerifier.create(currencyService.convert(from, to, amount))
                .expectError()
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
