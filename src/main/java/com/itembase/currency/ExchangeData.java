package com.itembase.currency;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.TreeMap;

// TODO: https://www.baeldung.com/json-multiple-fields-single-java-field
// TODO: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/validation.html
// TODO: https://www.baeldung.com/jackson-mapping-dynamic-object
// TODO: https://stackoverflow.com/questions/29746303/how-to-map-json-fields-to-custom-object-properties/29746495
// TODO: https://stackoverflow.com/questions/29746303/how-to-map-json-fields-to-custom-object-properties
public class ExchangeData {
    private Map<String, Double> rates;

    public Map<String, Double> getRates() {
        return this.rates;
    }

    /* Pseudocode
         insert:
             O(N) copy all values from original map to treemap
             vs
             O(1) variable set

         get:
             O(1) key->to value find
             vs
             O(N) search every key
     */
    public void setRates(Map<String, Double> rates){
        this.rates = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.rates.putAll(rates);
    }


    /* Pseudocode
            ExchangeData payload
               base
               to
               rates: Map<String,double>
               lastUpdate

              if(to==null || to.length==0) return InvalidArgumentException("To cannot be: " + to);

              var tmp = to.Replace(" ","");
              if(rates!=null && rates.containsKey(to, ignoreCase=true))
              {
                 return Mono.just(rates.get(to));
              }
              throw new RateNotFoundError("To not found: " + to);
         */
    public Double findRate(String to) {

        if(to==null || to.length()==0) throw new IllegalArgumentException("To cannot be: " + to);
        var tmp = to.replaceAll(" ","");
        var rates = getRates();
        if(rates!=null && rates.containsKey(to))
        {
            return rates.get(to);
        }
        throw new ApiException("RateNotFound", "To not found: " + to);
    }
}
