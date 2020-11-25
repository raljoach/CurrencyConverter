/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client used to make external API calls for exchange information
 */
public class ExchangeClient {
    private String exchangeUrl;
    private long timeout;

    /**
     * Constructs the client which makes external exchange API calls
     *
     * @param exchangeUrl base url for the external exchange API
     * @param timeout max timeout duration for any external API calls
     *
     * @returns conversion rate
     */
    public ExchangeClient(String exchangeUrl, long timeout) {

        this.exchangeUrl = exchangeUrl;
        this.timeout = timeout;
    }

    /**
     * Sends HTTP request for the conversion rate between currencies
     *
     * @param rateUrl the url appended to base url to make rate request
     * @param to the new currency to return the conversion rate for
     *
     * @returns conversion rate
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
                    System.out.println(payload.toString());
                    if(payload.error!=null) {
                        return Mono.error(new ApiException("ExchangeApiError",payload.error));
                    }
                    return Mono.just(payload.findRate(to));
                });
    }
}
