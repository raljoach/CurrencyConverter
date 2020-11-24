package com.itembase.currency;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends HttpResponse {
    private String errorCode;

    public ErrorResponse(){}

    public ErrorResponse(String errorCode, String message) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
