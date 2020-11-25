/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

/**
 * An exception class that indicates an error
 * has occurred with this application
 */
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

    public ApiException(String errorType, Throwable e, String internalError) {
        this(errorType, e.getMessage() + " " + internalError);
    }

    public String getErrorType() {
        return errorType;
    }


    private void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
