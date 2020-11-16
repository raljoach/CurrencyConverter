package com.itembase.currency;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// TODO:
//@Cacheable
public class ExchangeClient {
    private String exchangeUrl;
    public ExchangeClient(String exchangeUrl) {
        this.exchangeUrl = exchangeUrl;
    }

    // TODO:
    /* Pseudocode:
          create(url)
          .requestTime(this.timeout)

     */
    public Mono<Double> getRate(String rateUrl) {
        return WebClient
                .create(exchangeUrl)
                .get()
                .uri(rateUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Double.class);
    }
}
