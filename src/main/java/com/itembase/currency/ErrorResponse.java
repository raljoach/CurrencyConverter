package com.itembase.currency;

public class ErrorResponse {
    private String errorCode;
    private String message;

    public ErrorResponse(){}

    public ErrorResponse(String errorCode, String message) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
