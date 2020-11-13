package com.itembase.currency;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@ConfigurationProperties("exchange")
public class ApiConfig {
    private List<String> baseUrls = new ArrayList<String>();
    private List<String> rateUrls = new ArrayList<String>();

    public List<String> getBaseUrls(){
        return baseUrls;
    }

    public List<String> getRateUrls(){
        return rateUrls;
    }

    public void setBaseUrls(List<String> baseUrls){
        this.baseUrls = baseUrls;
    }

    public void setRateUrls(List<String> rateUrls){
        this.rateUrls = rateUrls;
    }

    public void shuffle() {
        int n = baseUrls.size();
        for (int current = 0; current < (n - 1); current++) {
            int other = ThreadLocalRandom.current().nextInt(current + 1, n);
            swap(baseUrls, current, other);
            swap(rateUrls, current, other);
        }
    }

    private void swap(List<String> items, int i, int j) {
        String tmp = items.get(i);
        items.set(i,items.get(j));
        items.set(j,tmp);
    }
}
