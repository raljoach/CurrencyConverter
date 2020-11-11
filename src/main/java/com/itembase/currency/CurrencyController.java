package com.itembase.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public Mono<ResponseEntity<?>> convert(@RequestBody ConversionRequest conversionRequest)
            throws JsonProcessingException {
        try {
            ConversionResponse conversionResponse = new ConversionResponse();
            conversionResponse.setFrom(conversionRequest.getFrom());
            conversionResponse.setTo(conversionRequest.getTo());
            conversionResponse.setAmount(conversionRequest.getAmount());
            currencyService.convert(
                    conversionRequest.getFrom(),
                    conversionRequest.getTo(),
                    conversionRequest.getAmount())
                    .subscribe(x -> conversionResponse.setConverted(x));
            return Mono.just(ResponseEntity.ok(conversionResponse));
        }
        catch(IllegalArgumentException ex)
        {
            ErrorResponse error = new ErrorResponse("BadInput", ex.getMessage());
            return Mono.just(
                    ResponseEntity.badRequest()
                    .body(error)
            );
        }
    }
}
