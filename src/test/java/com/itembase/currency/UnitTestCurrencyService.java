package com.itembase.currency;

import okhttp3.internal.connection.Exchange;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UnitTestCurrencyService {
    @Autowired
    CurrencyService currencyService;

    @MockBean
    ApiConfig mockApiConfig;

    @MockBean
    ExchangeClient mockExchangeClient;

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
        String baseUrl1 = "https://api1";
        String rateUrl1 = "/latest=<FROM>,<TO>";
        String from = "EUR";
        String to = "USD";
       //String exchangeUrl = baseUrl1.concat(rateUrl1.replaceAll("<FROM>",from).replaceAll("<TO>",to));
        //Mockito.when(new Exchange(any(String.class).thenReturn(baseUrl1)));
        when(mockApiConfig.getBaseUrls()).thenReturn(Arrays.asList(baseUrl1, "https://api2"));
        when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList(rateUrl1, "/<FROM>"));
        when(mockExchangeClient.getRate(rateUrl1, from, to)).thenReturn(Mono.just(1.25));
        StepVerifier.create(currencyService.convert(from, to, 40.0))
                .expectNext(40*1.25)
                .expectComplete()
                .verify();
    }

    //@Test
    void testConvertBaseUrl1Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenThrow(RuntimeException.class);
        when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList("/latest=<FROM>,<TO>", "/<FROM>"));
        throw new Exception("Not Implemented");
    }

    //@Test
    void testConvertBaseUrl2Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenReturn(Arrays.asList("https://api1", "https://api2"));
        when(mockApiConfig.getRateUrls()).thenThrow(RuntimeException.class);
        throw new Exception("Not Implemented");
    }

    //@Test
    void testConvertApi1Unavailable() throws Exception {
        doNothing().when(mockApiConfig).shuffle();
        when(mockApiConfig.getBaseUrls()).thenReturn(Arrays.asList("https://api1", "https://api2"));
        when(mockApiConfig.getRateUrls()).thenReturn(Arrays.asList("/latest=<FROM>,<TO>", "/<FROM>"));
        throw new Exception("Not Implemented");
    }
}
