package com.itembase.currency;

import reactor.core.publisher.Mono;

public class CurrencyService {
    public Mono<Double> convert(String base, String to, Double originalAmount) {
        Double res = -1.0;
        return Mono.just(res);
    }
}
