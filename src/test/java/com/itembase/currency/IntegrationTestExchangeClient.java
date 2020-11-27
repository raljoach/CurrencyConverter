package com.itembase.currency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
@WebFluxTest(ExchangeClient.class)
@EnableConfigurationProperties(value = ApiConfig.class)
public class IntegrationTestExchangeClient {
    ExchangeClient exchangeClient;

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
    public void testGetRate_Api1_Data_Format(){
        // arrange input
        exchangeClient = new ExchangeClient(String.format("http://localhost:%s",8181), 10000);
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();
        Double rate1 = TestUtils.getRandomRate();

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);

        // assert
        StepVerifier.create(exchangeClient.getRate(from, to))
                .expectNext(rate1)
                .verifyComplete();
    }

    @Test
    public void testGetRate_Api2_Data_Format(){
        // arrange input
        exchangeClient = new ExchangeClient(String.format("http://localhost:%s",7171), 10000);
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();
        Double rate1 = TestUtils.getRandomRate();

        // arrange mocks
        TestUtils.addExchangeApiServer2Response(from, to, rate1);

        // assert
        StepVerifier.create(exchangeClient.getRate(from, to))
                .expectNext(rate1)
                .verifyComplete();
    }
}
