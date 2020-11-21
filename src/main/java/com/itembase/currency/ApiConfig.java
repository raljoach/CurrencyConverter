package com.itembase.currency;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO: Integration test cases for ApiConfig
// TODO: https://www.baeldung.com/spring-cache-tutorial
// TODO: https://www.baeldung.com/java-caching-caffeine
// TODO: https://stackoverflow.com/questions/48769886/how-to-have-multiple-cache-manager-configuration-in-multiple-modules-projects-sp
// TODO: https://www.baeldung.com/spring-multiple-cache-managers
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

    /*
    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES);
    }

    @Bean
    @Primary
    public CacheManager cacheManager2() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("rates", "ratesMono","ratesRequest");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }
*/
/*
    // WORKS
    @Bean
    public CacheManager cacheManager() {
        //return new ConcurrentMapCacheManager("rates");
        var mgr = new ConcurrentMapCacheManager();
        mgr.setCacheNames(Arrays.asList("rates","ratesMono","ratesMono2","ratesRequest"));
        return mgr;
    }
    */

/*


    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "rates","ratesMono","ratesMono2","ratesRequest");
        caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
        return caffeineCacheManager;
    }


    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .weakKeys()
                .recordStats();
    }
*/
/*
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("currency");
        caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
        return caffeineCacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                //.expireAfterAccess(5, TimeUnit.MILLISECONDS)
                .weakKeys()
                .recordStats();
    }
*/
}
