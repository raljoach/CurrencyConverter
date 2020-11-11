package com.itembase.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CurrencyController.class)
public class UnitTestCurrencyController {

    @MockBean
    CurrencyService mockCurrencyService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testConvert() throws JsonProcessingException {
        // arrange
        List<String> currencyTypeList = new ArrayList<>();
        currencyTypeList.add("EUR");
        currencyTypeList.add("USD");

        String base = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        String to = currencyTypeList.get(ThreadLocalRandom.current().nextInt(currencyTypeList.size()));
        double originalAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        double convertedAmount = -1.0;

        if(base == to || originalAmount == 0)
        {
            convertedAmount = originalAmount;
        }
        else
        {
            convertedAmount = ThreadLocalRandom.current().nextDouble(0, 101);
        }

        Mono<Double> convertedAmountMono = Mono.just(convertedAmount);
        when(mockCurrencyService.convert(base,to,originalAmount)).thenReturn(convertedAmountMono);


        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(base);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);


        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(base);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(originalAmount);
        conversionResponse.setConverted(convertedAmount);

        // act, assert
        double finalConvertedAmount = convertedAmount;

        var theResponse =
        webTestClient.post()
                .uri("/currency/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(conversionRequest))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);


                var theBody = theResponse
                .expectBody();

                theBody.jsonPath("$.from").isEqualTo(base)
                        .jsonPath("$.to").isEqualTo(to)
                        .jsonPath("$.amount").isEqualTo(originalAmount)
                        .jsonPath("$.converted").isEqualTo(convertedAmount);


                theResponse
                        .expectBody(String.class)
                        .consumeWith(x->System.out.println("ACTUAL RESPONSE PAYLOAD: " + x.getResponseBody()));

                theResponse
                .expectBody(ConversionResponse.class)
                .consumeWith(
                        x-> {
                            ConversionResponse resp = x.getResponseBody();
                            try {
                                System.out.println("CONVERSION RESPONSE OBJECT: " + mapper.writeValueAsString(resp));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            assertEquals(base,resp.getFrom());
                            assertEquals(to,resp.getTo());
                            assertEquals(originalAmount,resp.getAmount());
                            assertEquals(finalConvertedAmount,resp.getConverted());
                        });

                theResponse
                        .expectBody(String.class)
                        .isEqualTo(mapper.writeValueAsString(conversionResponse));

    }
}
