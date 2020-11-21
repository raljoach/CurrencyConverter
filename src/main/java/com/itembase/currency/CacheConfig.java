package com.itembase.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig extends CachingConfigurerSupport
{
    @Bean
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("rate");
        caffeineCacheManager.setAllowNullValues(false);
        caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
        return caffeineCacheManager;
    }


    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(150)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                //.weakKeys()
                .removalListener(new CustomRemovalListener())
                .recordStats();
    }
/*
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }
*/
    class CustomRemovalListener implements RemovalListener<Object, Object> {
        @Override
        public void onRemoval(Object key, Object value, RemovalCause cause) {
            System.out.format("removal listener called with key [%s], cause [%s], evicted [%S]\n",
                    key, cause.toString(), cause.wasEvicted());
        }
    }
}
