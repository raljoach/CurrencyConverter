package com.itembase.currency;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

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

    public Double getRate(String base, String to){
        return getRateResponse(base, to).block();
    }

    public String outputExchangeData(String base){
        return ">> Get Mono<ExchangeData> base = " + base + " " + getExhangeDataResponse(base).subscribe(data -> System.out.println("PRINT data=" + data));
    }

    public String outputRate(String base, String to){
        return ">> Get Mono<Rate> base = " + base + ", to = " + to + " " + getRateResponse(base, to).subscribe(data -> System.out.println("PRINT data=" + data));
    }

    public String outputRates(String base){
        return ">> Get Flux<Rate> =  base=" + base + " " + getRatesResponse(base).subscribe(data -> System.out.println("PRINT key=" + data.getKey() + " value="+data.getValue()));
    }

    private Mono<ExchangeData> getExhangeDataResponse(String base) {
        Mono exchangeMono = webClient.get()
                .uri(createUrl(rateUrlFormat, base))
                .retrieve()
                .bodyToMono(ExchangeData2.class);
        return exchangeMono;
    }

    private Mono<Double> getRateResponse(String base, String to){
        return getExhangeDataResponse(base)
                .map(exData -> {
                    return exData.getRates().get(to);
                });
    }

    private Flux<Map.Entry<String, Double>[]> getRatesResponseOld(String base){
        return getExhangeDataResponse(base)
                .map(exData -> {
                    return exData.getRates().entrySet().toArray((Map.Entry<String, Double>[]) new Map.Entry[0]);
                }).flux();
    }

    private Flux<Map.Entry<String, Double>> getRatesResponse(String base){
        return getExhangeDataResponse(base)
                .map(exData -> {
                    return exData.getRates().entrySet().toArray((Map.Entry<String, Double>[]) new Map.Entry[0]);
                }).flatMapMany(Flux::fromArray);
    }

    public String getExchangeDataOld(String base){
        return ">> Get Mono<ExchangeData> = " + getExhangeDataResponse(base).doOnNext(data -> System.out.println("PRINT data=" + data)).subscribe();
    }

    private String createUrl(String pathUrlFormat, String value) {
        return pathUrlFormat + value;
    }
/*
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

    private String createUrl(String pathUrlFormat, String value) {
        return pathUrlFormat + value;
    }
*/
}
