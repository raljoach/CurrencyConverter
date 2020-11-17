package com.itembase.currency;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class ConversionResponse extends HttpResponse {

    private final ConversionData data = new ConversionData();

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

    public double getConverted() {
        return data.getConvertedAmount();
    }

    public void setConverted(double convertedAmount) {
        data.setConvertedAmount(convertedAmount);
    }

    public void validate() {
        var validator = new ConversionDataValidator();
        Errors errors = new BeanPropertyBindingResult(data, "person");
        validator.validate(data, errors);
        if (errors.hasErrors()) {
            throw new ApiException("BadInput",errors.toString());
        }
    }
}
