/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

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
    private int apiRetry;
    private int apiBackoff;
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

    public int getApiRetry() {
        return apiRetry;
    }

    public int getApiBackoff() {
        return apiBackoff;
    }

    public Duration getCacheDuration() {
        return this.cacheDuration;
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

    public void setApiRetry(int apiRetry) {
        this.apiRetry = apiRetry;
    }

    public void setApiBackoff(int apiBackoff) {
        this.apiBackoff = apiBackoff;
    }

    /**
     * Randomly rearranges the set of API endpoints read from application.properties file
     */
    public void shuffle() {
        if(this.useShuffle) {
            int n = baseUrls.size();
            for (int current = 0; current < (n - 1); current++) {
                int other = ThreadLocalRandom.current().nextInt(current + 1, n);
                swap(baseUrls, current, other);
                swap(rateUrls, current, other);
            }
        }
    }

    private void swap(List<String> items, int i, int j) {
        String tmp = items.get(i);
        items.set(i,items.get(j));
        items.set(j,tmp);
    }
}
