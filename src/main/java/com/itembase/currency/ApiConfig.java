package com.itembase.currency;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@EnableCaching
@ConfigurationProperties("exchange")
public class ApiConfig {
    private boolean useShuffle;
    private int requestTimeout=10_000;
    private Duration cacheDuration;
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

    public void setCacheDuration(long timeMs) {
        this.cacheDuration = Duration.ofMillis(timeMs);
    }

    public Duration getCacheDuration() {
        return this.cacheDuration;
    }
}
