package com.itembase.currency;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;

public class ExchangeGenericApiClient<T extends IExchangeData> {
    private String baseUrl;
    private WebClient webClient;
    private String rateUrlFormat;

    public ExchangeGenericApiClient(String baseUrl, String rateUrlFormat)
    {
        this.baseUrl = baseUrl;
        this.rateUrlFormat = rateUrlFormat;
        this.webClient = WebClient.create(baseUrl);
    }

    public Mono<T> exchangeData(String base) {

        Mono exchangeMono = webClient.get()
                .uri(createUrl(rateUrlFormat, base))
                .retrieve()
                .bodyToMono(((Class) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0]));
        return exchangeMono;
    }

    public String getExchangeData(String base){
        return ">> Get Mono<ExchangeData> = " + exchangeData(base).doOnNext(data -> System.out.println("PRINT data=" + data)).subscribe();
    }

    //public T getExchangeResult()

    private String createUrl(String pathUrlFormat, String value)
    {
        return pathUrlFormat + value;
    }
}
