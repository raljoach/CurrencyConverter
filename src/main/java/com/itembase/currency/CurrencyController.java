package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/convert")
    public Mono<ResponseEntity<ConversionResponse>> convert(@RequestBody ConversionRequest conversionRequest)
    {
        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(conversionRequest.getFrom());
        conversionResponse.setTo(conversionRequest.getTo());
        conversionResponse.setAmount(conversionRequest.getAmount());
        currencyService.convert(conversionRequest.getFrom(), conversionRequest.getTo(), conversionRequest.getAmount())
                .subscribe(x->conversionResponse.setConverted(x));
        return Mono.just(ResponseEntity.ok(conversionResponse));
    }
}
