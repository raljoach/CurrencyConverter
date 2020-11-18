package com.itembase.currency;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class ConversionRequest {
    private ConversionData data = new ConversionData();

    public String getFrom() {
        return data.getFrom();
    }

    public String getTo() {
        return data.getTo();
    }

    public double getAmount() {
        return data.getAmount();
    }

    public void setFrom(String from) {
        data.setFrom(from);
    }

    public void setTo(String to) {
        data.setTo(to);
    }

    public void setAmount(double amount) {
        data.setAmount(amount);
    }

    public String validate() {
        var validator = new ConversionDataValidator();
        Errors errors = new BeanPropertyBindingResult(data, "ConversionRequest");
        validator.validate(data, errors);
        //if (errors.hasErrors()) {
        //    throw new ApiException("BadInput",errors.toString());
        //}
        //return this;

        String res = null;
        if(errors.hasErrors()) {
            res = errors.toString();
        }
        return res;

    }
}
