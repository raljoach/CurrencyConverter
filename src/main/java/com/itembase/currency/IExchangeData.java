package com.itembase.currency;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

public interface IExchangeData {
    String getBase();
    //Mono<String> getBase();
    HashMap<String, Double> getRates();
    //Flux<Rate> getRates();
    String getLastUpdate();
    //Mono<LocalDateTime> getLastUpdate();
}
