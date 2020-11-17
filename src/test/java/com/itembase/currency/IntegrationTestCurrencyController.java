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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        TestUtils.setConfig(r, true);
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

        ConversionRequest conversionRequest = TestUtils.createConversionRequest(from, to, originalAmount);

        // arrange mocks
        Double rate0 = convertedAmount/originalAmount;
        //when(mockExchangeClient.getRate(any(String.class), any(String.class)))
        //        .thenReturn(Mono.just(rate0));

        TestUtils.addExchangeApiServer1Response(from, to, rate0);
        TestUtils.addExchangeApiServer2Response(from, to, rate0);

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
}
