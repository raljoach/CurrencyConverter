package com.itembase.currency;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ExchangeData2 extends BaseExchangeData {
    public String base_code;
    public HashMap<String, Double> conversion_rates;
    public String lastUpdate;
    public String nextUpdate;

    @Override
    public String getBase() { return base_code; }
    /*public Mono<String> getBase() {
        return null;
    }*/

    @Override
    public HashMap<String, Double> getRates() { return conversion_rates; }
    /*public Flux<Rate> getRates() {
        return null;
    }*/

    @Override
    public String getLastUpdate() { return lastUpdate; }
    //public Mono<LocalDateTime> getLastUpdate() { return null; }

    @Override
    public void setBase(String base) { this.base_code = base; }

    @Override
    public void setRates(HashMap<String, Double> rates){ this.conversion_rates = rates; }

    @Override
    public void setLastUpdate(String date){ this.lastUpdate = date; }

}
