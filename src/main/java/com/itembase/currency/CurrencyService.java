package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CurrencyService {
    private final ApiConfig apiConfig;

    @Autowired
    public CurrencyService(ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    public Mono<Double> convert(String base, String to, Double amount) {
        apiConfig.shuffle();
        Mono<Double> rateMono = getRate(base, to, amount);
        return rateMono.map(rate -> rate * amount);
    }

    private Mono<Double> getRate(String base, String to, Double amount) {
        return null;
    }





}
