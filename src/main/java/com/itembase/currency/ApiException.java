package com.itembase.currency;

public class ApiException extends RuntimeException {
    private String errorType;

    public ApiException(String errorType, Throwable e) {
        super(e.getMessage(),e);
        this.setErrorType(errorType);
    }

    public ApiException(String errorType, String message) {
        super(message);
        this.setErrorType(errorType);
    }

    private void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
