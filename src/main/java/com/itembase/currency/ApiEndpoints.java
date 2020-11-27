package com.itembase.currency;

import java.util.List;

public class ApiEndpoints {
    private List<ApiEndpoint> endpoints;

    public List<ApiEndpoint> getEndpoints(){
        return this.endpoints;
    }

    public void setEndpoints(List<ApiEndpoint> endpoints){
        this.endpoints = endpoints;
    }
}
