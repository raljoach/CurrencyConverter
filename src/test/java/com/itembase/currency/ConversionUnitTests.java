package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.web.servlet.function.RequestPredicates.method;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = SpringTestConfig.class)
public class ConversionUnitTests<EmployeeService> {
    @Autowired
    private ExchangeApiClient1 exchangeApiClient1 = new ExchangeApiClient1();
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject() {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://api.exchangeratesapi.io/latest?base=EUR"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(emp))
                ));

        Double val = exchangeApiClient1.getRate("EUR", "USD");
        mockServer.verify();
        Assert.assertEquals(emp, employee);
    }
}
