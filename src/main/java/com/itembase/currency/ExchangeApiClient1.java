package com.itembase.currency;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ExchangeApiClient1 {
    private String baseUrl;
    private WebClient webClient;
    private String rateUrlFormat;

    public ExchangeApiClient1()
    {
        this.baseUrl = "https://api.exchangeratesapi.io";
        this.rateUrlFormat="/latest?base=";
        this.webClient = WebClient.create(baseUrl);
    }

    public Mono<ExchangeData> exchangeData(String base) {

        Mono exchangeMono = webClient.get()
                .uri(createUrl(rateUrlFormat, base))
                .retrieve()
                .bodyToMono(ExchangeData.class);
        return exchangeMono;
    }

    public Mono<Double> rate(String base, String to){
        return exchangeData(base)
                .map(exData -> {
                    return exData.getRates().get(to);
                });
    }

    public Flux<Map.Entry<String, Double>[]> rates(String base){
        return exchangeData(base)
                .map(exData -> {
                    return exData.getRates().entrySet().toArray((Map.Entry<String, Double>[]) new Map.Entry[0]);
                }).flux();
    }

    public Flux<Map.Entry<String, Double>> rates2(String base){
        return exchangeData(base)
                .map(exData -> {
                    return exData.getRates().entrySet().toArray((Map.Entry<String, Double>[]) new Map.Entry[0]);
                }).flatMapMany(Flux::fromArray);
    }

    public String getExchangeData(String base){
        return ">> Get Mono<ExchangeData> = " + exchangeData(base).doOnNext(data -> System.out.println("PRINT data=" + data)).subscribe();
    }

    public String getExchangeData2(String base){
        return ">> Get Mono<ExchangeData> = " + exchangeData(base).subscribe(data -> System.out.println("PRINT data=" + data));
    }

    public void printRates(String base){
        rates2(base).subscribe(data -> System.out.println("PRINT key=" + data.getKey() + " value="+data.getValue()));
    }

    private String createUrl(String pathUrlFormat, String value)
    {
        return pathUrlFormat + value;
    }
}
