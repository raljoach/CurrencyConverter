package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ExchangeApiClient1UnitTests {

    private ExchangeApiClient1 exchangeApiClient1;
    private ObjectMapper objectMapper = new ObjectMapper();

    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        exchangeApiClient1 = new ExchangeApiClient1(baseUrl,"/latest?base=");
    }

    @Test
    public void testExchangeApiClient1GetRate() throws Exception {
        Double expectedValue = 1.25;
        var data = new ExchangeData();
        data.setBase("EUR");
        var map = new HashMap<String, Double>();
        map.put("USD",expectedValue);
        data.setRates(map);
        data.setDate(LocalDateTime.now().toString());

        var json = objectMapper.writeValueAsString(data);
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        var value = exchangeApiClient1.getRate("EUR", "USD");
        Assert.assertEquals(expectedValue, value);
    }
// TODO: TEST: convert USD to USD
// TODO: TEST: double.Max, double.Min, 0, 1, fraction(1-8 places), whole number, whole+decimal(1-8 places)
        // TODO from: randomly select from set of "USD", "EUR", "PND" ....
        // TODO to: randomly select from set of "USD", "EUR", "PND" ....
        // TODO amount: 0.00 to 1000000.00 or double.MaxValue

        /*
        Double expectedValue = 1.25;
        var data = new ExchangeData();
        data.setBase("EUR");
        var map = new HashMap<String, Double>();
        map.put("USD",expectedValue);
        data.setRates(map);
        data.setDate(LocalDateTime.now().toString());


        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://api.exchangeratesapi.io/latest?base=EUR")))
                .andExpect((RequestMatcher) method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(data)));

        Double val = exchangeApiClient1.getRate("EUR", "USD");
        mockServer.verify();
        Assert.assertEquals(expectedValue, val);

         */

}
