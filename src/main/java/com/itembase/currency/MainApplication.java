package com.itembase.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

// TODO: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-application-availability-managing
// TODO: https://stackoverflow.com/questions/50986816/how-to-handle-http-requests-in-a-microservice-event-driven-architecture
// TODO: https://www.xspdf.com/resolution/50037856.html
// TODO: https://dreamchaser3.tistory.com/17
// TODO: https://www.hellojava.com/a/61192.html
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ApiConfig.class/*, CacheConfig.class*/})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
