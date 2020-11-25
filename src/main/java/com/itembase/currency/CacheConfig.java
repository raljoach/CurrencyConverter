/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration settings related to caching of results
 * to external api calls are configured here
 */
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
                .recordStats();
    }
}
