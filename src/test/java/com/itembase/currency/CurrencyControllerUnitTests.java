package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyController.class)
public class CurrencyControllerUnitTests {

    @MockBean
    private ExchangeApiService1 service;

    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testConvertEurToUsd() throws Exception {
        var convertRequest = new ConversionRequest();
        String from="EUR", to="USD";
        double amount = 5;
        Double rate = 1.25;
        convertRequest.setFrom(from);
        convertRequest.setTo(to);
        convertRequest.setAmount(amount);

        var convertedAmount = rate * amount;

        var expectedResponse = new ConversionResponse();
        expectedResponse.setFrom(from);
        expectedResponse.setTo(to);
        expectedResponse.setAmount(amount);
        expectedResponse.setConverted(convertedAmount);

        Mockito.when(service.getRate("EUR", "USD")).thenReturn(rate);
/*
        webClient.post()
                .uri("/currency/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(convertRequest))
                .exchange()
                .expectStatus().isCreated()
        .expectBody(ConversionResponse.class)
        .isEqualTo(expectedResponse);
*/
        //given(service.getAllEmployees()).willReturn(allEmployees);

        var json = objectMapper.writeValueAsString(convertRequest);
        mvc.perform(post("/currency/convert")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .equals(expectedResponse);
        /*
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(alex.getName())));*/
        Mockito.verify(service, times(1)).getRate("EUR", "USD");
    }
}



