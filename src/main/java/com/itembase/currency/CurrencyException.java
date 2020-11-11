package com.itembase.currency;

public class CurrencyException extends RuntimeException {
    private String errorCode;

    public CurrencyException(String errorCode, String message) {
        super(message);
        setErrorCode(errorCode);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
