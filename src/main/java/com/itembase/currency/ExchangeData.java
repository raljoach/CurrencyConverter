/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.TreeMap;

/**
 * Stores data returned by external API calls for exchange information
 */
public class ExchangeData {

    @JsonProperty("base")
    @JsonAlias("base_code")
    private String base;

    @JsonProperty("date")
    @JsonAlias("time_last_update_utc")
    private String lastUpdate;

    @JsonProperty("rates")
    @JsonAlias("conversion_rates")
    private Map<String, Double> rates;

    @JsonProperty("error")
    public String error;

    public String getBase() {
        return this.base;
    }

    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public Map<String, Double> getRates() {
        return this.rates;
    }

    public String getError() {
        return this.error;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setLastUpdate(String dateTime) {
        this.lastUpdate = dateTime;
    }

    public void setRates(Map<String, Double> rates){
        this.rates = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.rates.putAll(rates);
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the rate corresponding to the destination currency
     * @param to destination currency
     */
    public Double findRate(String to) {

        if(to==null || to.length()==0) throw new IllegalArgumentException("To cannot be: " + to);
        var tmp = to.replaceAll(" ","");
        var rates = getRates();
        if(rates!=null && rates.containsKey(tmp))
        {
            return rates.get(tmp);
        }
        throw new ApiException("RateNotFound", "To not found: " + to);
    }
}
