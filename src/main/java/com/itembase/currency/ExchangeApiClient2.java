package com.itembase.currency;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ExchangeApiClient2 {
    private String baseUrl;
    private WebClient webClient;
    private String rateUrlFormat;

    public ExchangeApiClient2()
    {
        this.baseUrl = "https://v6.exchangerate-api.com/v6/68931214b500a7a0f882192f";
        this.rateUrlFormat = "/latest/";
        this.webClient = WebClient.create(baseUrl);
    }

    public Mono<ExchangeData2> exchangeData(String base) {

        Mono exchangeMono = webClient.get()
                .uri(createUrl(rateUrlFormat, base))
                .retrieve()
                .bodyToMono(ExchangeData2.class);
        return exchangeMono;
    }

    public String getExchangeData(String base){
        return ">> Get Mono<ExchangeData> = " + exchangeData(base).doOnNext(data -> System.out.println("PRINT data=" + data)).subscribe();
    }

    private String createUrl(String pathUrlFormat, String value)
    {
        return pathUrlFormat + value;
    }
}
