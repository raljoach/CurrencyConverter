/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Base class used to represent all REST controller responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
