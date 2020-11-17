package com.itembase.currency;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    private static MockWebServer exchangeApiServer1;
    private static MockWebServer exchangeApiServer2;
    private static Map<String, Double> rates = new HashMap<String, Double>();
    private static String[] currencies;

    static {
        rates.put("EUR", randomRate());
        rates.put("USD", randomRate());
        rates.put("AED", randomRate());
        rates.put("ARS", randomRate());
        rates.put("AUD", randomRate());
        rates.put("BGN", randomRate());
        rates.put("BRL", randomRate());
        rates.put("BSD", randomRate());
        rates.put("CAD", randomRate());
        rates.put("CHF", randomRate());
        rates.put("CLP", randomRate());
        rates.put("CNY", randomRate());
        rates.put("COP", randomRate());
        rates.put("CZK", randomRate());
        rates.put("DKK", randomRate());
        rates.put("DOP", randomRate());
        rates.put("EGP", randomRate());
        rates.put("FJD", randomRate());
        rates.put("GBP", randomRate());
        rates.put("GTQ", randomRate());
        rates.put("HKD", randomRate());
        rates.put("HRK", randomRate());
        rates.put("HUF", randomRate());
        rates.put("IDR", randomRate());
        rates.put("ILS", randomRate());
        rates.put("INR", randomRate());
        rates.put("ISK", randomRate());
        rates.put("JPY", randomRate());
        rates.put("KRW", randomRate());
        rates.put("KZT", randomRate());
        rates.put("MVR", randomRate());
        rates.put("MXN", randomRate());
        rates.put("MYR", randomRate());
        rates.put("NOK", randomRate());
        rates.put("NZD", randomRate());
        rates.put("PAB", randomRate());
        rates.put("PEN", randomRate());
        rates.put("PHP", randomRate());
        rates.put("PKR", randomRate());
        rates.put("PLN", randomRate());
        rates.put("PYG", randomRate());
        rates.put("RON", randomRate());
        rates.put("RUB", randomRate());
        rates.put("SAR", randomRate());
        rates.put("SEK", randomRate());
        rates.put("SGD", randomRate());
        rates.put("THB", randomRate());
        rates.put("TRY", randomRate());
        rates.put("TWD", randomRate());
        rates.put("UAH", randomRate());
        rates.put("UYU", randomRate());
        rates.put("ZAR", randomRate());
        currencies = new String[rates.size()];
        rates.keySet().toArray(currencies);
    }

    public static String[] getCurrencies() {
        return currencies;
    }

    public static Map<String, Double> getRates() {
        return rates;
    }

    public static void startExchangeApiServers() throws IOException {
        exchangeApiServer1 = new MockWebServer();
        exchangeApiServer1.start(8181);
        exchangeApiServer2 = new MockWebServer();
        exchangeApiServer2.start(7171);
    }

    public static void stopExchangeApiServers() throws IOException {
        exchangeApiServer1.shutdown();
        exchangeApiServer2.shutdown();
    }

    public static void addExchangeApiServer1Response(String from, String to, double rate){
        addExchangeApiServer1Response(creatApiDataResponse1(from, createRates(to, rate)));
    }

    private static void addExchangeApiServer1Response(String response){
        exchangeApiServer1.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json"));
    }

    public static void addExchangeApiServer1ErrorResponse(String status, String response) {
        exchangeApiServer1.enqueue(new MockResponse()
                .setStatus(status)
                .setBody(response)
                .addHeader("Content-Type", "application/json"));
    }

    public static void addExchangeApiServer2Response(String from, String to, double rate){
        addExchangeApiServer2Response(creatApiDataResponse2(from, createRates(to, rate)));
    }

    private static void addExchangeApiServer2Response(String response){
        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json"));
    }

    public static void addExchangeApiServer2ErrorResponse(String status, String response) {
        exchangeApiServer2.enqueue(new MockResponse()
                .setStatus(status)
                .setBody(response)
                .addHeader("Content-Type", "application/json"));
    }

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

    public static ErrorResponse createErrorResponse(String message, String errorCode){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        return errorResponse;
    }

    public static String creatApiDataResponse1(String base, Map<String, Double> rates) {
        StringBuilder result = new StringBuilder("{\n");
        result.append("\"rates\": { \n");
        result.append(createStr(rates));
        result.append("},\n\"");
        result.append("\"base\": \""+base+"\", \n");
        result.append("\"date\": \"2020-11-02\" \n");
        result.append("}");
        return result.toString();
    }

    public static String creatApiDataResponse2(String base, Map<String, Double> rates) {
        StringBuilder result = new StringBuilder("{\n");
        appendKeyValueToStr(result, "result", "\"success\"");
        appendKeyValueToStr(result, "documentation", "\"success\"");
        appendKeyValueToStr(result, "terms_of_use", "\"success\"");
        appendKeyValueToStr(result, "time_last_update_unix", "\"success\"");
        appendKeyValueToStr(result, "time_last_update_utc", "\"success\"");
        appendKeyValueToStr(result, "time_next_update_unix", "\"success\"");
        appendKeyValueToStr(result, "time_next_update_utc", "\"success\"");
        appendKeyValueToStr(result, "base_code", "\""+base+"\"");
        appendKeyValueToStr(result, "conversion_rates", "{\n" + createStr(rates) + "\n}");
        result.append("}\n");
        return result.toString();
    }

    public static double randomRate() {
        return random().nextDouble(0, 101);
    }

    /* Pseudocode:
         var length = lowerCaseStr.size();
         if(length==0) return lowerCaseStr;
         var index = random().next(0,length);
         char ch = lowerCaseStr.get(index);
         char upperChar = ch.toUpperCase();

         return lowerCaseStr.subString(0, index).concat(upperChar).concat(lowerCaseStr.subString(index+1);
     */
    public static String randomUpperCase(String lowerCaseStr) {
        var length = lowerCaseStr.length();
        if(length==0) return lowerCaseStr;
        var index = random().nextInt(0, length);
        Character ch = lowerCaseStr.charAt(index);
        Character upperChar = ch.toString().toUpperCase().charAt(0);

        return lowerCaseStr.substring(0, index).concat(upperChar.toString()).concat(lowerCaseStr.substring(index+1));
    }

    public static ThreadLocalRandom random(){
        return ThreadLocalRandom.current();
    }

    /* Pseudocode:
        result.append(result).append(": ").append(value);

     */
    private static void appendKeyValueToStr(StringBuilder result, String key, String value) {
        result.append(result).append(": ").append(value).append("\n");
    }

    private static String createStr(Map<String, Double> rates) {
        StringBuilder result = new StringBuilder();
        var itr = rates.entrySet().iterator();
        var count=0;
        while(itr.hasNext())
        {
            var entry = itr.next();
            var currency = entry.getKey();
            var rate = entry.getValue();
            if(++count>1)
            {
                result.append(",\n");
            }
            result.append("\"" +  currency + "\":" + rate);
        }
        result.append("\n\"");
        return result.toString();
    }

    private static Map<String, Double> createRates(String to, double rate) {
        Map<String, Double> rates = new HashMap<String, Double>();
        rates.put(to, rate);
        return rates;
    }

    public static void setConfig(DynamicPropertyRegistry r, Boolean useShuffle) {
        //r.add("requestTimeout", () -> "1000");
        r.add("useShuffle", () -> useShuffle.toString());
        r.add("exchange.baseUrls[0]", () -> String.format("http://localhost:%s",8181));
        //exchangeApiServer1.getPort()));
        r.add("exchange.baseUrls[1]", () -> String.format("http://localhost:%s",7171));
        //exchangeApiServer2.getPort()));
    }
}
