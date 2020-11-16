package com.itembase.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// TODO: https://www.baeldung.com/spring-cache-tutorial
// TODO: https://howtodoinjava.com/spring-boot2/spring-boot-cache-example/
// TODO: https://stackoverflow.com/questions/50986816/how-to-handle-http-requests-in-a-microservice-event-driven-architecture
//@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties(ApiConfig.class)
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
