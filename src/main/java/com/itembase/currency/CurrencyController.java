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

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    /* IT: CurrencyController.convert
     * T1: happyPath base=valid, to=valid, rate=X, amount=Y => base=/,to=/,amt=/,converted=X*Y
     * T2: cached converted value
     * T3: expired cached converted value
     */
    /* UT: CurrencyController.convert
       T1: from=empty,null,blank,padded spaces,valid,invalid,wrong,doesn't exist
       T2: from=valid, to=empty,null,blank,padded spaces,valid,invalid,wrong,doesn't exist
       T3: from=valid, to=valid,
           amount=empty,blank,null,padded spaces,valid=0,whole(0-INF).decimals(1-5),invalid=negative,
                  wrong=alpha,equivalence/boundary:negative,positive,-INF,+INF,-INF-1,+INF+1,0,edge cases
     */
    @PostMapping("/convert")
    public Mono<ResponseEntity<HttpResponse>> convert(@RequestBody ConversionRequest conversionRequest) {
        try {
            ConversionResponse conversionResponse = new ConversionResponse();
            conversionResponse.setFrom(conversionRequest.getFrom());
            conversionResponse.setTo(conversionRequest.getTo());
            conversionResponse.setAmount(conversionRequest.getAmount());
            HttpResponse httpResponse = conversionResponse;

            //ok().build()
            var serviceResponse = currencyService.convert(
                    conversionRequest.getFrom(),
                    conversionRequest.getTo(),
                    conversionRequest.getAmount()).doOnNext(x->x.doubleValue());

            var notFound = new ResponseEntity<ConversionResponse>(HttpStatus.NOT_FOUND);
            var notFoundMono = Mono.just(notFound);
            var res = serviceResponse
                    .map(convertValue -> {
                        conversionResponse.setConverted(convertValue);
                        var responseEntity = ok(httpResponse);
                        return responseEntity;
                    });//.block());
                    //.subscribe();
                    //.defaultIfEmpty(ResponseEntity.notFound().build());
            return res;
            //return res;
                    //.defaultIfEmpty(ResponseEntity.notFound().build());;

            //.subscribe();



/*            return serviceResponse
                    .flatMap(i -> Mono.just(i)
                            .doOnNext(x->conversionResponse.setConverted(x))
                            .then(Mono.just(ResponseEntity.ok(conversionResponse)))
                    )

                    .doOnSubscribe(x->Mono.just(ResponseEntity.ok(conversionResponse)));

 */
/*
            return serviceResponse
                    //.log()
                    //.flatMap(x->  conversionResponse.setConverted(x);  )
                    .doOnNext(x->conversionResponse.setConverted(x))
                    //.thenReturn(ResponseEntity.ok(conversionResponse))
                    .dematerialize();
                    //.doOnSubscribe(ok(conversionResponse));
//                    .doOnSubscribe();
                    //.subscribe(x -> conversionResponse.setConverted(x));
            //return Mono.just(ResponseEntity.ok(conversionResponse));
  */      }
        catch(Throwable ex)
        {
            return handleError(ex);
        }
    }

    private Mono<ResponseEntity<HttpResponse>> handleError(Throwable ex)
    {
        String errorCode="UnhandledException";
        String message = ex.getMessage();
        if(ex instanceof IllegalArgumentException )
        {
            errorCode="BadInput";
        }
        else if(ex instanceof CurrencyException)
        {
            errorCode = ((CurrencyException) ex).getErrorCode();
        }

        if(message == null || message.isBlank() || message.isEmpty())
        {
            message = ex.toString();
        }
        ErrorResponse error = new ErrorResponse(errorCode, message);
        return Mono.just(
                ResponseEntity.badRequest()
                        .body(error)
        );
    }
}
