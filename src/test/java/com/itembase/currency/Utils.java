package com.itembase.currency;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
    public static ConversionRequest createConversionRequest(String from, String to, double originalAmount) {
        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setFrom(from);
        conversionRequest.setTo(to);
        conversionRequest.setAmount(originalAmount);
        return conversionRequest;
    }

    public static ConversionResponse createConversionResponse(String from, String to, double amount, double converted) {
        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setFrom(from);
        conversionResponse.setTo(to);
        conversionResponse.setAmount(amount);
        conversionResponse.setConverted(converted);
        return conversionResponse;
    }

    public static String createApiDataFormat1(String base, String to, Map<String, Double> rates)
    {
        StringBuilder result = new StringBuilder("{\n");
        result.append("\"rates\": { \n");
        var itr = rates.entrySet().iterator();
        var count=0;
        while(itr.hasNext())
        {
            var currency = itr.next().getKey();
            var rate = itr.next().getValue();
            if(++count>1)
            {
                result.append(",\n");
            }
            result.append("\"" +  rate + "\":" + rate);
        }
        result.append("\n\"");
        result.append("},\n\"");
        result.append("\"base\": \""+base+"\", \n");
        result.append("\"date\": \"2020-11-02\" \n");
        result.append("}");
        return result.toString();
    }
}
