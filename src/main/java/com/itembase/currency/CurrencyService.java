package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CurrencyService {
    private final ApiConfig apiConfig;

    @Autowired
    public CurrencyService(ApiConfig apiConfig)
    {
        this.apiConfig = apiConfig;
    }

    /* T1: base = null
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
    public Mono<Double> convert(String base, String to, Double amount) {
        apiConfig.shuffle();
        Mono<Double> rateMono = getRate(base, to, amount);
        return rateMono.map(rate -> rate * amount);
    }

    /* T1:  both clients available
     * T2:  client 1 available, client 2 unavailable   => UnavailableApiError
     * T3:  client 1 unavailable, client 2 available   => rate returned
     * T4:  client 1 timeout, client 2 available       => rate returned
     * T5:  client 1 timeout, client 2 unavailable     => UnavailableApiError
     * T6:  client 1 timeout, client 2 timeout         => UnavailableApiError
     * T7:  client 1 doesn't have base, client 2 has rate => rate returned
     * T7:  client 1 doesn't have base, client 2 doesn't have base => RateUnavailabeError(Reason: 'B' not found)
     * T8:  client 1 doesn't have base, client 2 doesn't have to   => RateUnavailabeError(Reason: 'T' not found)
     * DUPE(T7) client 1 doesn't have to, client 2 doesn't have base => RateUnavailabeError(Reason: 'T' not found)
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
          tryFirstClient()
             .onErrorResume(trySecondClient()
                            .onError(throw ApiUnavailableException()))

     */
    private Mono<Double> getRate(String base, String to, Double amount) {
        return null;
    }





}
