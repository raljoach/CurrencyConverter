package com.itembase.currency;

import org.springframework.stereotype.Service;

@Service
public class ExchangeApiService1 {
    public Double getRate(String base, String to){
        return new ExchangeApiClient1().getRate(base, to);
    }

}
