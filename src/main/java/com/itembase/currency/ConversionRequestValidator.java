package com.itembase.currency;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ConversionRequestValidator implements Validator {

    /**
     * This Validator validates *just* Person instances
     */
    public boolean supports(Class clazz) {
        return ConversionRequest.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
        ConversionRequest p = (ConversionRequest) obj;
        validateAmount(e, p.getAmount());
        validateFrom(e, p.getFrom());
        validateTo(e, p.getTo());
    }

    private void validateTo(Errors e, String to) {
        ValidationUtils.rejectIfEmptyOrWhitespace(e, to, "BadInput", "to is empty");
    }

    private void validateFrom(Errors e, String from) {
        ValidationUtils.rejectIfEmptyOrWhitespace(e, from, "BadInput", "from is empty");
    }

    private void validateAmount(Errors e, double amount) {
        if(amount<0){
            e.rejectValue("amount", "amount is less than zero");
        }
    }
}