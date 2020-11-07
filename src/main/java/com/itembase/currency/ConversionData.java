package com.itembase.currency;

public class ConversionData {
    private String from;
    private String to;
    private double amount;
    private double converted;

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
        return converted;
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

    public void setConverted(double converted) {
        this.converted = converted;
    }

    @Override
    public String toString() {
        return "{" + "from='" + from +'\'' +
                ", to='" + to + '\'' +
                ", amount='" + amount + '\'' +
                ", converted='" + converted + '\'' +
                '}';
    }
}
