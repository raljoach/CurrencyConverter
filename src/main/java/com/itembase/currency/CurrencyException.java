/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

/**
 * An exception class that indicates an error
 * has occurred during a currency conversion operation
 */
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
