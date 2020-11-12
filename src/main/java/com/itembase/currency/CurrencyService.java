package com.itembase.currency;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {
    private List<ApiConfig> apiList = new ArrayList<>();
    public CurrencyService(){
        initialize();
    }

    public Mono<Double> convert(String base, String to, Double amount) {
        shuffle();
        Mono<Double> rateMono = getRate(base, to, amount);
        return rateMono.map(rate->rate*amount);
    }

    private Mono<Double> getRate(String base, String to, Double amount) {
        return null;
    }


    private void shuffle() {
    }

    private void initialize() {

    }


}
