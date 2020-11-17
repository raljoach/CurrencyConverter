package com.itembase.currency;

public class ApiException extends RuntimeException {
    private String errorType;

    public ApiException(String errorType, Throwable e) {
        super(/*errorType + " " +*/ e.getMessage(),e);
        System.out.println("errorType1: " + errorType);
        System.out.println("Throwable inner1: " + e.getMessage());
        this.setErrorType(errorType);
    }

    public ApiException(String errorType, String message) {
        super(/*errorType + " " +*/ message);
        System.out.println("errorType2: " + errorType);
        System.out.println("DEBUG ApiException message2: " + message);
        this.setErrorType(errorType);
    }

    public String getErrorType() {
        return errorType;
    }


    private void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
