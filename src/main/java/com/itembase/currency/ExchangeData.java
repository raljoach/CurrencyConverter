package com.itembase.currency;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ExchangeData extends BaseExchangeData {
    private String base;
    private HashMap<String, Double> rates;
    private String date;

    @Override
    public String getBase() { return base; }

    @Override
    public void setBase(String base) { this.base = base; }

    /*public Mono<String> getBase() {
        return null;
    }*/

    @Override
    public HashMap<String, Double> getRates() { return rates; }

    @Override
    public void setRates(HashMap<String, Double> rates) { this.rates = rates; }

    /*public Flux<Rate> getRates() {
        return null;
    }*/

    @Override
    public String getLastUpdate() { return getDate(); }

    @Override
    public void setLastUpdate(String date) { setDate(date); }

    /*public Mono<LocalDateTime> getLastUpdate() {
        return null;
    }*/

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }


}
