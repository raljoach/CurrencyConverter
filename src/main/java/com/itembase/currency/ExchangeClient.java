package com.itembase.currency;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

// TODO:
//@Cacheable
public class ExchangeClient {
    private String exchangeUrl;
    private long timeout;
    public ExchangeClient(String exchangeUrl, long timeout) {

        this.exchangeUrl = exchangeUrl;
        this.timeout = timeout;
    }



    /* Pseudocode:
          create(url)
          .requestTime(this.timeout)
          .bodyToMono(ExchangeData)
          .flatMap( payload->{
               return findRate(payload, to);
          })
     */
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
                    if(payload.error!=null) {
                        return Mono.error(new ApiException("ExchangeApiError",payload.error));
                    }
                    return Mono.just(payload.findRate(to));
                });
    }

}
