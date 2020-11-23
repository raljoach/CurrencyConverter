package com.itembase.currency;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ExchangeClient {
    private String exchangeUrl;
    private long timeout;
    public ExchangeClient(String exchangeUrl, long timeout) {

        this.exchangeUrl = exchangeUrl;
        this.timeout = timeout;
    }

    public Mono<Double> getRate(String rateUrl, String to) {
        return WebClient
                .create(exchangeUrl)
                .get()
                .uri(rateUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ExchangeData.class)
                .timeout(Duration.ofMillis(timeout))
                .flatMap(payload->{
                    System.out.println(payload.toString());
                    if(payload.error!=null) {
                        return Mono.error(new ApiException("ExchangeApiError",payload.error));
                    }
                    return Mono.just(payload.findRate(to));
                });
    }
}
