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
    public Mono<ResponseEntity<HttpResponse>> convert(@RequestBody ConversionRequest conversionRequest) {
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
        catch(Throwable ex)
        {
            return handleError(ex);
        }
    }

    private Mono<ResponseEntity<HttpResponse>> handleError(Throwable ex)
    {
        String errorCode="UnknownError";
        String message = ex.getMessage();
        if(ex instanceof IllegalArgumentException )
        {
            errorCode="BadInput";
        }
        else if(ex instanceof CurrencyException)
        {
            errorCode = ((CurrencyException) ex).getErrorCode();
        }
        else if(ex instanceof NullPointerException)
        {
            errorCode="InternalError";
            message = "";
        }
        ErrorResponse error = new ErrorResponse(errorCode, message);
        return Mono.just(
                ResponseEntity.badRequest()
                        .body(error)
        );
    }
}
