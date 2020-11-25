/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * This is a an application which hosts
 * a reactive REST controller endpoint /currency/conversion
 * which performs conversion of an amount between currencies
 * @param args
 */
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ApiConfig.class})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
