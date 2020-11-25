/************************************************
 *
 * Author: Ralph Joachim
 *
 ************************************************/
package com.itembase.currency;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Performs validation on ConversionData fields
 */
public class ConversionDataValidator implements Validator {
    public boolean supports(Class clazz) {
        return ConversionData.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ConversionData p = (ConversionData) obj;
        validateAmount(e, p.getAmount());
        validateFrom(e, p.getFrom());
        validateTo(e, p.getTo());
        validateConvertedAmount(e, p.getConvertedAmount());
    }

    private void validateConvertedAmount(Errors e, double convertedAmount) {
        if(convertedAmount<0){
            e.rejectValue("converted amount", "converted amount is less than zero");
        }
    }

    private void validateTo(Errors e, String to) {
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "to", "BadInput", "to is empty");
    }

    private void validateFrom(Errors e, String from) {
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "from", "BadInput", "from is empty");
    }

    private void validateAmount(Errors e, double amount) {
        if(amount<0){
            e.rejectValue("amount", "amount is less than zero");
        }
    }
}