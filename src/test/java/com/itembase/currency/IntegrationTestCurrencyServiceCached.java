package com.itembase.currency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CurrencyService.class)
@EnableConfigurationProperties(value = ApiConfig.class)
@Import({CacheConfig.class})
public class IntegrationTestCurrencyServiceCached {
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
        TestUtils.setConfig(r, useShuffle, 50000);
    }

    @Test
    void test_GetRateMono_CacheExpired() throws InterruptedException {
        // arrange input
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();

        Double rate1 = TestUtils.getRandomRate();
        Double rate2 = TestUtils.getRandomRate();

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);
        TestUtils.addExchangeApiServer1Response(from, to, rate2);

        // act
        var rateAmt1Mono = currencyService.getRateMono(from, to);
        var rateAmt2Mono = currencyService.getRateMono(from, to);

        // assert

        StepVerifier
                .create(rateAmt1Mono)
                .consumeNextWith(
                            x->{
                                assertEquals(rate1, x.doubleValue());
                            }
                    )
                .verifyComplete();


        StepVerifier
                .create(rateAmt2Mono)
                .consumeNextWith(
                        x->{
                            assertEquals(rate1, x.doubleValue());
                        }
                )
                .verifyComplete();

        Thread.sleep(5000);

        // act
        var rate3Mono = currencyService.getRateMono(from, to);
        var rate4Mono = currencyService.getRateMono(from, to);

        // assert

        StepVerifier
                .create(rate3Mono)
                .consumeNextWith(
                        x->{
                            assertEquals(rate2, x.doubleValue());
                        }
                )
                .verifyComplete();


        StepVerifier
                .create(rate4Mono)
                .consumeNextWith(
                        x->{
                            assertEquals(rate2, x.doubleValue());
                        }
                )
                .verifyComplete();
    }

    @Test
    void test_GetRateMono_UsingCache() throws InterruptedException {
        // arrange input
        String from = TestUtils.getRandomCurrency();
        String to = TestUtils.getRandomCurrency();

        Double rate1 = TestUtils.getRandomRate();
        Double rate2 = TestUtils.getRandomRate();

        // arrange mocks
        TestUtils.addExchangeApiServer1Response(from, to, rate1);
        TestUtils.addExchangeApiServer1Response(from, to, rate2);

        // act
        var rateAmt1Mono = currencyService.getRateMono(from, to);
        var rateAmt2Mono = currencyService.getRateMono(from, to);

        // assert

        StepVerifier
                .create(rateAmt1Mono)
                .consumeNextWith(
                        x->{
                            assertEquals(rate1, x.doubleValue());
                        }
                )
                .verifyComplete();


        StepVerifier
                .create(rateAmt2Mono)
                .consumeNextWith(
                        x->{
                            assertEquals(rate1, x.doubleValue());
                        }
                )
                .verifyComplete();
    }
}
