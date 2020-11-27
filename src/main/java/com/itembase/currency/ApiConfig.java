/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Application configuration settings read from application.properties
 * are read and set in the object properties below
 * Note: Only properties that begin with exchange. are stored here
 */
@Configuration
@EnableCaching
@ConfigurationProperties("exchange")
public class ApiConfig {
    private boolean useShuffle;
    private int requestTimeout=10_000;
    private Duration cacheDuration;

    private ApiEndpoint[] endpoints;
    private List<String> baseUrls = new ArrayList<String>();
    private List<String> rateUrls = new ArrayList<String>();

    public boolean getUseShuffle() {
        return useShuffle;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public List<String> getBaseUrls() {
        return baseUrls;
    }

    public List<String> getRateUrls() {
        return rateUrls;
    }


    public ApiConfig() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        endpoints = mapper.readValue(new File("src/main/resources/exchange-api.json"),ApiEndpoint[].class);
    }
    public void setUseShuffle(boolean useShuffle) {
        this.useShuffle = useShuffle;
    }

    public void setRequestTimeout(int requestTimeout) {

        this.requestTimeout = requestTimeout;
    }

    public void setBaseUrls(List<String> baseUrls) {
        this.baseUrls = baseUrls;
    }

    public void setRateUrls(List<String> rateUrls){
        this.rateUrls = rateUrls;
    }

    public void setCacheDuration(long timeMs) {
        this.cacheDuration = Duration.ofMillis(timeMs);
    }

    public Duration getCacheDuration() {
        return this.cacheDuration;
    }

    /**
     * Randomly rearranges the set of API endpoints read from application.properties file
     */
    public void shuffle() {
        if(this.useShuffle) {
            int n = endpoints.length;
            for (int current = 0; current < (n - 1); current++) {
                int other = ThreadLocalRandom.current().nextInt(current + 1, n);
                swap(endpoints, current, other);
            }
        }
    }
    public void shuffleOld() {
        if(this.useShuffle) {
            int n = baseUrls.size();
            for (int current = 0; current < (n - 1); current++) {
                int other = ThreadLocalRandom.current().nextInt(current + 1, n);
                swapOld(baseUrls, current, other);
                swapOld(rateUrls, current, other);
            }
        }
    }

    public ApiEndpoint getEndpoint(int i){
        return endpoints[i];
    }

    private void swap(ApiEndpoint[] items, int i, int j) {
        var tmp = items[i];
        items[i] = items[j];
        items[j] = tmp;
    }

    private void swapOld(List<String> items, int i, int j) {
        String tmp = items.get(i);
        items.set(i,items.get(j));
        items.set(j,tmp);
    }
}
