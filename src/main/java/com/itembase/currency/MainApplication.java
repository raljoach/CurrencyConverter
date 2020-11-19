package com.itembase.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

// TODO: https://projectreactor.io/docs/extra/snapshot/api/reactor/cache/CacheMono.html
// TODO: https://projectreactor.io/docs/extra/3.1.4.RELEASE/api/reactor/cache/CacheMono.html
// TODO: https://www.baeldung.com/spring-cache-tutorial
// TODO: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-caching
// TODO: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-application-availability-managing
// TODO: https://stackoverflow.com/questions/52171581/how-is-spring-flux-cache-item-managed
// TODO: https://howtodoinjava.com/spring-boot2/spring-boot-cache-example/
// TODO: https://medium.com/@eranda/springboot-caching-with-memcached-and-redis-f0c26ea66132
// TODO: https://stackoverflow.com/questions/50986816/how-to-handle-http-requests-in-a-microservice-event-driven-architecture
// TODO: https://www.foreach.be/blog/spring-cache-annotations-some-tips-tricks
// TODO: https://docs.spring.io/spring-framework/docs/3.1.x/spring-framework-reference/html/cache.html
// TODO: https://stackoverflow.com/questions/48156424/spring-webflux-and-cacheable-proper-way-of-caching-result-of-mono-flux-type
// TODO: https://www.xspdf.com/resolution/50037856.html
// TODO: https://stackoverflow.com/questions/48156424/spring-webflux-and-cacheable-proper-way-of-caching-result-of-mono-flux-type
// TODO: https://dreamchaser3.tistory.com/17
// TODO: https://www.hellojava.com/a/61192.html
// TODO: https://spring.io/blog/2019/12/13/flight-of-the-flux-3-hopping-threads-and-schedulers
// TODO: https://www.callicoder.com/spring-boot-task-scheduling-with-scheduled-annotation/
// TODO: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/EnableCaching.html
// TODO: https://www.dineshonjava.com/spring-cache-tutorial/

@SpringBootApplication
@EnableConfigurationProperties({ApiConfig.class/*, CacheConfig.class*/})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
