package com.itembase.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.ok;

// TODO: https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/html/boot-features-logging.html
// TODO: https://www.baeldung.com/spring-bucket4j
// TODO: https://docs.spring.io/spring-batch/docs/current/reference/html/retry.html
// TODO: https://www.baeldung.com/spring-retry
// TODO: https://dzone.com/articles/spring-retry-way-to-handle-failures
// TODO: https://grokonez.com/reactive-programming/reactor/reactor-handle-error
// TODO: https://blog.trifork.com/2019/03/13/retry-functionality-in-a-reactive-programming-context/
// TODO: https://opensource.zalando.com/restful-api-guidelines/
// TODO: https://howtodoinjava.com/spring-boot2/rest/enableasync-async-controller/
// TODO: https://stackoverflow.com/questions/55227086/spring-flux-and-the-async-annotation
// TODO: https://ilyazinkovich.github.io/2018/10/26/demystifying-spring-magic-async.html
// TODO: https://www.baeldung.com/spring-mvc-async-vs-webflux
// TODO: https://www.logicbig.com/how-to/code-snippets/jcode-spring-framework-async-and-enableasync.html
// TODO: https://www.logicbig.com/tutorials/spring-framework/spring-core/async-annotation.html
// TODO: https://programmer.ink/think/variable-passing-of-reactor-asynchronous-thread.html
// TODO: https://theboreddev.com/combining-multiple-api-calls-with-completablefuture/
/* application.properties
   logging.file=my.log
   logging.path=/logs
   requestTimeout=10000
*/

@RestController
@RequestMapping("currency")
@Validated
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    //TODO: https://www.baeldung.com/spring-security-cache-control-headers
    //TODO: https://www.springboottutorial.com/spring-boot-versioning-for-rest-services
    //.cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))

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
        //try {

            var inputRequestMono =
                    Mono.just(conversionRequest);


            var serviceResponse = inputRequestMono.flatMap(request-> {
                var errors = request.validate();
                if(errors!=null)
                {
                    Mono.error(new ApiException("BadInput", errors));
                }
                return currencyService.convert(
                        request.getFrom(),
                        request.getTo(),
                        request.getAmount()).doOnNext(x -> x.doubleValue());
            });

            return serviceResponse
                    .flatMap(convertValue -> {
                        ConversionResponse conversionResponse = new ConversionResponse();
                        conversionResponse.setFrom(conversionRequest.getFrom());
                        conversionResponse.setTo(conversionRequest.getTo());
                        conversionResponse.setAmount(conversionRequest.getAmount());
                        HttpResponse httpResponse = conversionResponse;
                        conversionResponse.setConverted(convertValue);
                        conversionResponse.validate();
                        var responseEntity = ok(httpResponse);
                        return Mono.just(responseEntity);
                    })
                    .onErrorResume(e->{
                        return handleError(e);
                    })
                    ;//.block());
                    //.subscribe();
                    //.defaultIfEmpty(ResponseEntity.notFound().build());
            //return res;
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
        }
        catch(Throwable ex)
        {
            return handleError(ex);
        }*/
    }

    //TODO: @GetMapping /status

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
        else if(ex instanceof ApiException)
        {
            errorCode = ((ApiException) ex).getErrorType();
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
