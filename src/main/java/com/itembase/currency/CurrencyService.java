package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeoutException;

// TODO: https://developer.okta.com/blog/2018/09/24/reactive-apis-with-spring-webflux
// @Log4j2
@Service
//@Cacheable(cacheNames="currency")
//@CacheConfig(cacheNames = "employee-cache")
@CacheConfig(cacheNames={"rate"}
//, keyGenerator = "customKeyGenerator"
)
//@CacheConfig(cacheNames={"currency"})
public class CurrencyService {
    private final ApiConfig apiConfig;

    @Autowired
    public CurrencyService(ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    /* UT: CurrencyService.convert
    * T1: base = null
    *  T2: base = empty
    *  T3: base = blanks
    *  T4: base = valid, " EUR "
    *  T5: base doesn't exist "ZYL"
    *  T6: base is more than 3 characters => "EURR"
    *  T7: base is non-alpha => "@34adfa/\"
    *  T8: base=valid, to=T1..T7
    *  T9: base=to, amount=X
    *  T9: amount=-INF => UnderFlowError
    *  T10: amount=0 => InvalidAmountError
    *  T11: amount +INFO => OverFlowError
    *  T12: amount=X, rate=Y  => X*Y
    *  T13: amount=X.Z, rate=Y  => X.Z*Y
    *  T14: amount=X.BC, rate=Y  => X.BC*Y
    *  T15: amount=X.BCD, rate=Y  => InvalidAmountError
    *  T12: amount= HDEFGX.BC, rate=Y  => HDEFGX.BC*Y
    *  T12: amount= HDEFGX.BC, rate=Y.AI  => HDEFGX.BC*Y.AI
    *  T13: amount= HDEFGX.BC, rate=Y.AIW  => Round(2 decimals,HDEFGX.BC*Y.AIW)
    */

    /* Pseudocode:
         shuffle()
         getRate()
         return rate * amount
     */
    public Mono<Double> convert(String from, String to, Double amount) {
        apiConfig.shuffle();
        Mono<Double> rateMono = getCachedRateMono(from, to);
        return rateMono.map(rate -> round(rate * amount, 2));

        //var rate = getRateValue(from, to);
        //return Mono.just(round(rate * amount, 2));
    }
    

    // TODO: https://stackoverflow.com/questions/62329617/webflux-webclient-re-try-with-different-url
    // TODO: https://www.baeldung.com/spring-webclient-simultaneous-calls
    // TODO: https://technology.amis.nl/2019/08/09/apache-camel-and-spring-boot-calling-multiple-services-in-parallel-and-merging-results/
    // TODO: https://docs.spring.io/spring-integration/docs/4.3.23.BUILD-SNAPSHOT/reference/html/messaging-endpoints-chapter.html
    // TODO: https://stackoverflow.com/questions/21670451/how-to-send-multiple-asynchronous-requests-to-different-web-services
    // TODO: https://stackoverflow.com/questions/48762680/how-to-call-two-or-more-web-services-or-rest-in-parallel-with-project-reactor-an
    // TODO: https://stackoverflow.com/questions/50203875/how-to-use-spring-webclient-to-make-multiple-calls-simultaneously
    // TODO: https://stackoverflow.com/questions/48762680/how-to-call-two-or-more-web-services-or-rest-in-parallel-with-project-reactor-an
    // TODO: https://stackoverflow.com/questions/43592072/do-multiple-calls-to-different-methods-in-parallel-in-spring
    // TODO: https://stackoverflow.com/questions/21670451/how-to-send-multiple-asynchronous-requests-to-different-web-services
    // TODO: https://www.baeldung.com/spring-security-async-principal-propagation
    // TODO: https://medium.com/sipios/how-to-make-parallel-calls-in-java-springboot-application-and-how-to-test-them-dcc27318a0cf
    // TODO: https://stackoverflow.com/questions/38904723/best-way-to-make-multiple-asynchronous-calls-to-same-web-service
    // TODO: https://stackoverflow.com/questions/43592072/do-multiple-calls-to-different-methods-in-parallel-in-spring
    // TODO: https://stackoverflow.com/questions/48762680/how-to-call-two-or-more-web-services-or-rest-in-parallel-with-project-reactor-an
    // TODO: https://stackoverflow.com/questions/17777880/how-to-send-parallel-get-requests-and-wait-for-result-responses
    // TODO: https://stackoverflow.com/questions/21670451/how-to-send-multiple-asynchronous-requests-to-different-web-services
    // TODO: https://niels.nu/blog/2016/spring-async-rest.html
    // TODO: https://theboreddev.com/combining-multiple-api-calls-with-completablefuture/
    // TODO: https://dzone.com/articles/parallel-and-asynchronous-programming-in-java-8
    // TODO: https://nickolasfisher.com/blog/How-to-Make-Concurrent-Service-API-Calls-in-Java-Using-Spring-Boot
    // TODO: https://www.sipios.com/blog-tech/how-to-make-parallel-calls-in-java-springboot-application-and-how-to-test-them
    // TODO: https://stackoverflow.com/questions/17777880/how-to-send-parallel-get-requests-and-wait-for-result-responses
    // TODO: https://medium.com/sipios/how-to-make-parallel-calls-in-java-springboot-application-and-how-to-test-them-dcc27318a0cf
    // TODO: https://stackoverflow.com/questions/56467836/how-to-make-multiple-spring-webclient-calls-in-parallel-and-wait-for-the-result
    // TODO:
    /* UT: CurrencyService.getRate
     * [X] T1:  both clients available
     * T2:  client 1 available, client 2 unavailable   => UnavailableApiError
     * [X] T3:  client 1 unavailable, client 2 available   => rate returned
     * [X] T4:  client 1 timeout, client 2 available       => rate returned
     * [X] T5:  client 1 timeout, client 2 unavailable     => UnavailableApiError
     * [X] T6:  client 1 timeout, client 2 timeout         => UnavailableApiError
     * T7:  client 1 doesn't have from, client 2 has rate => rate returned
     * T7:  client 1 doesn't have from, client 2 doesn't have from => RateUnavailabeError(Reason: 'B' not found)
     * T8:  client 1 doesn't have from, client 2 doesn't have to   => RateUnavailabeError(Reason: 'T' not found)
     * DUPE(T7) client 1 doesn't have to, client 2 doesn't have from => RateUnavailabeError(Reason: 'T' not found)
     * DUPE(T8) client 1 doesn't have to, client 2 doesn't have to
     * T11: client 1 doesn't have to, client 2 has rate => rate returned
     * T12: client 1 has new rate, client 2 has older rate
     * T13: client 1 has older rate, client 2 has newer rate
     *
     * // IGNORE
     * T14: client1 returns negative rate, client 2 returns positive rate => rate returned [WILL ASSUME RATE IS ALWAYS >0]
     * T15: client1 returns negative rate, client 2 returns negative rate => ApiError('Rates are negative') [NOT SURE]
     * T15: client1 returns rate=0, client 2 returns non-zero rate [NOT SURE: WILL ASSUME RATE IS ALWAYS >0]

     * T15: client1 returns rate=0, client 2 returns non-zero rate [WILL ASSUME RATE IS ALWAYS >0]
     *
     * // DO
     * T18: client1 returns X.Y rate, client 2 returns Z.D rate => return X.Y rate
     * T19: client1 returns X.0 rate
     * T20: client1 returns X.Y rate
     * T21: client1 returns X.YZ rate
     * T22: client1 returns X.YZA rate
     * T22: client1 returns X.YZ94444444 rate
     * T22: client1 returns X.YZ95444444 rate
     * T22: client1 returns X.YZ94999999 rate
     * T22: client1 returns X.YZ44999999 rate
     * T22: client1 returns X.YZ49999999 rate
     * T23: client1 returns BX.YZA rate
     * T24: client1 returns +INF.INF rate
     * T25: client1 returns +INF rate
     * T26: client1 returns -INF rate
     * T27: client returns +INF+1 rate => OverFlowError
     * T28: client returns -INF-1 rate => UnderFlowError
     *
     *
     * T21: client1 return cached value, client1 returns new value after cache expires
     */
    /* Pseudocode:
          tryClient(0)
             .onErrorResume(tryClient(1)
                            .onError(throw ApiUnavailableException()))

     */

    //@Cacheable(value="rates", key="#p0.concat(#p1)", sync=true)
   // @Cacheable(
   //         cacheNames = "rates",
   //         sync=true//,
            //key="#from.concat(#to)"
   // )
    public Double getRateValue(String from, String to) {
        return getRateMono(from, to).block();
    }

    //@Cacheable("rateMono")
/*
    String key = "myId";
    LoadingCache<String, Object> graphs = Caffeine
            .newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> createExpensiveGraph(key));

    Mono<Double> cachedRate = CacheMono
            .lookup(graphs.asMap(), key)
            .onCacheMissResume(repository.findOneById(key));
*/

    //@Cacheable(
    //        cacheNames = "ratesMono2",
            //key="#from.concat(#to)",
    //        sync = true)
    public Mono<Double> getCachedRateMono(String from, String to) {
        return getRateMono(from,to).cache(apiConfig.getCacheDuration());
    }
/*
    @Cacheable(
            cacheNames={"rates","ratesMono","ratesMono2","ratesRequest"}
            //key="#from.concat(#to)",
            //sync = true
    )

 */
    //@Cacheable
    /*
    @Cacheable
            (value = "rate",
            //key="{ #root.methodName, #from, #to }",
            keyGenerator = "customKeyGenerator",
            sync = true)*/
    @Cacheable
            //(key="#from")
    public Mono<Double> getRateMono(String from, String to) {
        //try {

            return makeRequestForRate(0, from, to)
                    .log()
                    .onErrorResume(e -> {
                                System.out.println("E1: " +e.getMessage());
                                return makeRequestForRate(1, from, to)
                                        .log()
                                        /*.onErrorResume(
                                                e2 -> {

                                                    System.out.println("E2: " +e2.getMessage());
                                                    return Mono.error(
                                                            new ApiException(
                                                                    //"RateError2 " + apiClientUrl2 + rateUrl2,
                                                                    "RateError2",
                                                                    apiClientUrl + rateUrl + " "+ e2.toString() + e2.getMessage()));
                                                                    //e2));

                                                })*/
                                        .flatMap(rate2 -> {
                                                    System.out.println("Using rate2: " + rate2);
                                                    return Mono.just(rate2).cache(apiConfig.getCacheDuration());
                                                }
                                        );
                            }
                    /*
                .flatMap(x->{
                    System.out.println("Using rate3: " + x); return Mono.just(x);
                })*/
                    )
                    .flatMap(rate1 -> {
                        System.out.println("Using rate1: " + rate1);
                        return Mono.just(rate1).cache(apiConfig.getCacheDuration());
                    });
        /*}
        catch(Exception ex)
        {
            return Mono.error(
                    new ApiException(
                            "RateError",
                            ex));
        }*/
    }

    /* Pseudocode:
          apiClientUrl = apiConfig.baseUrls[i];
          rateUrl = rateUrl(apiConfig.rateUrls[i], from, to);
          webClient = new WebClient(apiClientUrl);
          return webClient.get()
             .uri(rateUrl)
             .contentType("application/json")
             .subscribe();
     */

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /*@Cacheable(
            cacheNames = "ratesRequest",
            //key="#from.concat(#to)",
            sync = true)*/
    public Mono<Double> makeRequestForRate(int i, String from, String to){

        String apiClientUrl = apiConfig.getBaseUrls().get(i);
        String rateUrl = rateUrl(apiConfig.getRateUrls().get(i), from, to);

        return new ExchangeClient(apiClientUrl, apiConfig.getRequestTimeout()).getRate(rateUrl, to).cache(apiConfig.getCacheDuration())
                .onErrorResume(e->{
                    if(e instanceof WebClientResponseException)
                    {
                        var internalError = ((WebClientResponseException)e).getResponseBodyAsString();
                        return Mono.error(new ApiException("RateError", e, internalError));
                    }
                    else if(e instanceof TimeoutException)
                    {
                        return Mono.error(new ApiException(
                                "RateError", apiClientUrl + rateUrl + " TimeoutException"
                        ));
                    }
                    return Mono.error(new ApiException(
                            "RateError", /*apiClientUrl + rateUrl + " "+ e.toString() +*/ e//.getMessage()
                    ));
                });
    }

    /* Pseudocode:
          return format.replaceAll("<FROM>", base).replaceAll("<TO>",to)
     */
    private String rateUrl(String format, String from, String to) {
        return format.replaceAll("<FROM>", from).replaceAll("<TO>",to);
    }

}
