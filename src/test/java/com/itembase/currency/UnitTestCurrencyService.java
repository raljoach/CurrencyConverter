package com.itembase.currency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
public class UnitTestCurrencyService {
    @Autowired
    CurrencyService currencyService;

    @MockBean
    ExchangeClient mockExchangeClient;

    @Test
    void testConvert_Using_Cached_Rate() throws InterruptedException {
        // arrange input
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();
        double amount = TestUtils.getRandomAmount();
        Double rate1 = TestUtils.getRandomRate();
        Double rate2 = TestUtils.getRandomRate();

        // arrange mocks
        //TestUtils.addExchangeApiServer1Response(from, to, rate1);
        //TestUtils.addExchangeApiServer1Response(from, to, rate2);
        when(mockExchangeClient.getRate(any(String.class),any(String.class)))
                .thenReturn(Mono.just(rate1));

        // act
        var convAmt1 = currencyService.convert(from, to, amount);

        // arrange mocks
        when(mockExchangeClient.getRate(any(String.class),any(String.class)))
                .thenReturn(Mono.just(rate2));

        var convAmt2 = currencyService.convert(from, to, amount);

        // assert
        var expectConvAmt = TestUtils.currencyRound(rate1*amount);
        assertEquals(expectConvAmt,convAmt1);
        assertEquals(expectConvAmt,convAmt2);
        Thread.sleep(5000);

        // act
        var convAmt3 = currencyService.convert(from, to, amount);
        var convAmt4 = currencyService.convert(from, to, amount);

        // assert
        var expectConvAmt2 = TestUtils.currencyRound(rate2*amount);
        assertEquals(expectConvAmt2,convAmt3);
        assertEquals(expectConvAmt2,convAmt4);

    }
}
