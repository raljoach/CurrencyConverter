package com.itembase.currency;

public class ConversionRequest {
    private ConversionData data = new ConversionData();

    public ConversionRequest(){}
    public ConversionRequest(String from, String to, double amount){
        this.setFrom(from);;
        this.setTo(to);
        this.setAmount(amount);
    }
    public String getFrom() {
        return data.getFrom();
    }

    public String getTo() {
        return data.getTo();
    }

    public double getAmount() {
        return data.getAmount();
    }

    public ConversionData getData() {
        return data;
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

    @Override
    public String toString() {
        return data.toString();
    }


    /*private String from;
    private String to;
    private double amount;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
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

    @Override
    public String toString() {
        return "{" + "from='" + from +'\'' +
               ", to='" + to + '\'' +
               ", amount='" + amount + '\'' +
               '}';
    }*/
}
