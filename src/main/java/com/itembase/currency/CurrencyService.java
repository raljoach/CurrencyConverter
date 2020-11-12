package com.itembase.currency;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CurrencyService {
    public Mono<Double> convert(String base, String to, Double originalAmount) {
        Double res = -1.0;
        return Mono.just(res);
    }

    public void setApiList(List<ApiConfig> apiList) {
    }
}
