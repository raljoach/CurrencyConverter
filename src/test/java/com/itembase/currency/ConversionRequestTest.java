package com.itembase.currency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

// Test cases for /currency/convert calls
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConversionRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testConvertShouldReturnCorrectValues() {
        String from="EUR", to="USD";
        double amount = 5;
        ConversionRequest convertRequest = new ConversionRequest(from,to,amount);
        ConversionResponse convertResponse = this.restTemplate.postForObject(
                "http://localhost:" + port + "/currency/convert",
                convertRequest,
                ConversionResponse.class);
        assert convertResponse != null;
        assertThat(Objects.equals(convertResponse.getFrom(), from));
        assertThat(Objects.equals(convertResponse.getTo(), to));
        assertThat(convertResponse.getAmount()==amount);
        assertThat(convertResponse.getConverted()>0);
        assertThat(convertResponse.getConverted()!=amount);
    }

}
