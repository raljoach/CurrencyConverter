package com.itembase.currency;

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
