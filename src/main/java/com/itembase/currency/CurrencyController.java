/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.ok;

/**
 * REST controller for currency calls
 */
@RestController
@RequestMapping("currency")
@Validated
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    /**
     * Returns status indicating controller is available
     */
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Server up");
    }

    /**
     * Converts currency amount
     *
     * @param conversionRequest payload for performing currency conversion
     *
     * @returns converted amount response
     */
    @PostMapping("/convert")
    public Mono<ResponseEntity<HttpResponse>> convert(@RequestBody ConversionRequest conversionRequest) {
            var inputRequestMono =
                    Mono.just(conversionRequest);


            var serviceResponse = inputRequestMono.flatMap(request-> {
                var errors = request.validate();
                if(errors!=null)
                {
                    return Mono.error(new ApiException("BadInput", errors));
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
                    });
    }

    /**
     * Exception Handler for controller
     *
     * @param ex Thrown exception to be handle
     *
     * @returns HTTP response to be returned to the REST controller client
     */
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
