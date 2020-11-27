package com.itembase.currency;

public class ApiEndpoint {

    private String baseUrl;
    private String rateUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRateUrl(String rateUrl) {
        this.rateUrl = rateUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getRateUrl() {
        return this.rateUrl;
    }
}
