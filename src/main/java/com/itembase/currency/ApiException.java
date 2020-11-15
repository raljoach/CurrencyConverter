package com.itembase.currency;

public class ApiException extends RuntimeException {
    public String errorType;
    public ApiException(String errorType, Throwable e) {
        super(e.getMessage(),e);
        this.errorType = errorType;
    }
}
