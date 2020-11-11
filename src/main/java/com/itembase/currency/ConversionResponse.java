package com.itembase.currency;

public class ConversionResponse {
    private String from;
    private String to;
    private double amount;
    private double convertedAmount;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public double getConverted() {
        return convertedAmount;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setConverted(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
