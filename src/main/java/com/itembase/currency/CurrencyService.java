/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Service class that performs all currency operations
 */
@Service
@CacheConfig(cacheNames={"rate"})
public class CurrencyService {
    private final ApiConfig apiConfig;

    @Autowired
    public CurrencyService(ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    /**
     * Converts amount from one currency to another currency
     *
     * @param from starting currency
     * @param to new currency
     * @param amount value to be converted in starting currency
     *
     * @returns amount in new currency that is equivalent to starting currency
     */
    public Mono<Double> convert(String from, String to, Double amount) {
        apiConfig.shuffle();
        Mono<Double> rateMono = getCachedRateMono(from, to);
        return rateMono.map(rate -> round(rate * amount, 2));
    }


    /**
     * Gets the conversion rate between currencies
     *
     * @param from starting currency
     * @param to new currency
     *
     * @returns conversion rate
     */
    @Cacheable
    public Mono<Double> getRateMono(String from, String to){
        if(apiConfig.getBaseUrls().size()<1) {
            throw new ApiException("ConfigurationError", "No external exchange API endpoints configured");
        }

        var currentRequest =
                makeRequestForRate(0, from, to);
        if(apiConfig.getApiRetry()>0) {
            currentRequest = currentRequest.retryWhen(
                    Retry.backoff(apiConfig.getApiRetry(), Duration.ofSeconds(apiConfig.getApiBackoff()))
            );
        }
        for(int i=1; i<apiConfig.getBaseUrls().size(); i++) {
            int finalI = i;
            if(apiConfig.getApiRetry()>0) {
                currentRequest = currentRequest.onErrorResume(
                        t -> Exceptions.isRetryExhausted(t),
                        t -> {
                            var theNextRequest = makeRequestForRate(finalI, from, to);
                            theNextRequest.retryWhen(
                                    Retry.backoff(
                                            apiConfig.getApiRetry(), Duration.ofSeconds(apiConfig.getApiBackoff()))
                            );
                            return theNextRequest;
                        });
            }
            else {
                currentRequest = currentRequest.onErrorResume(
                        e->{
                            return makeRequestForRate(finalI, from, to);
                        }
                );
            }
        }
        return currentRequest;
    }

    private Mono<Double> getCachedRateMono(String from, String to) {
        return getRateMono(from,to).cache(apiConfig.getCacheDuration());
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Gets the conversion rate between currencies
     *
     * @param i use the external API at this position
     * @param from starting currency
     * @param to new currency
     *
     * @returns conversion rate
     */
    private Mono<Double> makeRequestForRate(int i, String from, String to){

        String apiClientUrl = apiConfig.getBaseUrls().get(i);
        String rateUrl = rateUrl(apiConfig.getRateUrls().get(i), from, to);

        return new ExchangeClient(apiClientUrl,
                apiConfig.getRequestTimeout()).getRate(rateUrl, to)
                .cache(apiConfig.getCacheDuration())
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
