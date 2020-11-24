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

@Service
@CacheConfig(cacheNames={"rate"})
public class CurrencyService {
    private final ApiConfig apiConfig;

    @Autowired
    public CurrencyService(ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    public Mono<Double> convert(String from, String to, Double amount) {
        apiConfig.shuffle();
        Mono<Double> rateMono = getCachedRateMono(from, to);
        return rateMono.map(rate -> round(rate * amount, 2));
    }

    public Mono<Double> getCachedRateMono(String from, String to) {
        return getRateMono(from,to).cache(apiConfig.getCacheDuration());
    }

    @Cacheable
    public Mono<Double> getRateMono(String from, String to) {
            return makeRequestForRate(0, from, to)
                    .onErrorResume(e -> {

                                return makeRequestForRate(1, from, to)
                                        .flatMap(rate2 -> {
                                                    return Mono.just(rate2).cache(apiConfig.getCacheDuration());
                                                }
                                        );
                            }
                    )
                    .flatMap(rate1 -> {
                        return Mono.just(rate1).cache(apiConfig.getCacheDuration());
                    });
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Mono<Double> makeRequestForRate(int i, String from, String to){

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

    private String rateUrl(String format, String from, String to) {
        return format.replaceAll("<FROM>", from).replaceAll("<TO>",to);
    }
}
