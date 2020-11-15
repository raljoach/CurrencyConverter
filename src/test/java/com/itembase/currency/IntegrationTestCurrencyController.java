package com.itembase.currency;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
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
import java.util.concurrent.ThreadLocalRandom;

/* Functional Tests of /currency/convert api of CurrencyController
   using mock Exchange API Web Servers implementation
 */

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CurrencyController.class)
@Import({CurrencyService.class, ExchangeClient.class})
@EnableConfigurationProperties(value = ApiConfig.class)
public class IntegrationTestCurrencyController {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CurrencyService currencyService;

    private static MockWebServer exchangeApiServer1;
    private static MockWebServer exchangeApiServer2;

    @BeforeAll
    static void setup() throws IOException {
        exchangeApiServer1 = new MockWebServer();
        exchangeApiServer1.start();
        exchangeApiServer2 = new MockWebServer();
        exchangeApiServer2.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        exchangeApiServer1.shutdown();
        exchangeApiServer2.shutdown();
    }

    // TODO: V1 This requires a application.yml file in src/main/resources with the below values
    // TODO: V2 I want to use a application-properties.json file instead of application.yml
    //       which may require me to use @ActiveProfiles
    // TODO: V3 I may want to use a config.json/app.config file whereby in the application.yml, I have
    //       app.configuration=config.json/app.config (WON'T WORK because I can't dynamically change exchange api ports)
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        // This is the V1 way
        r.add("exchange.baseUrls[0]", () -> String.format("https://localhost:%s",exchangeApiServer1.getPort()));
        r.add("exchange.baseUrls[1]", () -> String.format("https://localhost:%s",exchangeApiServer2.getPort()));
    }

    /*
    @BeforeEach
    void initialize() {
        // arrange mocks
        String baseUrl1 = String.format("https://localhost:%s",exchangeApiServer1.getPort());
        String baseUrl2 = String.format("https://localhost:%s",exchangeApiServer2.getPort());
        currencyService.setApiList(createApiList(baseUrl1, baseUrl2));
    }
*/

    private List<ApiConfig> createApiList(String baseUrl1, String baseUrl2) {
        return null;
    }

    @Test
    void testConvert() {
        // arrange input, output
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        String from = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(from == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);

        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(from);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(originalAmount);
        conversionResponse.setConverted(convertedAmount);

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
