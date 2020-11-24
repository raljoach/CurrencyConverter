package com.itembase.currency;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    private static MockWebServer exchangeApiServer1;
    private static MockWebServer exchangeApiServer2;
    private static Map<String, Double> rates = new HashMap<String, Double>();
    private static String[] currenciesArr;
    private static List<String> currenciesList;

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
        currenciesArr = new String[rates.size()];
        rates.keySet().toArray(currenciesArr);
        currenciesList = Arrays.asList(currenciesArr);
    }

    public static String[] getCurrencies() {
        return currenciesArr;
    }

    public static String getRandomCurrency() {
        return currenciesList.get(random().nextInt(currenciesList.size()));
    }

    public static double getRandomAmount(){
        return currencyRound(random().nextDouble(0, 1_000_000_001));
    }

    public static double getRandomRate(){
        return random().nextDouble(0, 101);
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

    public static void addExchangeApiServer1ErrorResponse(int status, String response) {
        exchangeApiServer1.enqueue(new MockResponse()
                .setResponseCode(status)
                //.setStatus("HTTP 1.1 "+status + " FOO")
                .setBody(response)
                .addHeader("Content-Type", "application/json")
                );
    }

    public static void addExchangeApiServer2Response(String from, String to, double rate){
        addExchangeApiServer2Response(creatApiDataResponse2(from, createRates(to, rate)));
    }

    private static void addExchangeApiServer2Response(String response){
        exchangeApiServer2.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json"));
    }

    public static void addExchangeApiServer2ErrorResponse(int status, String response) {
        exchangeApiServer2.enqueue(new MockResponse()
                .setResponseCode(status)
                //.setStatus("HTTP 1.1 "+status + " FOO")
                .setBody(response)
                .addHeader("Content-Type", "application/json")
        );
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
        result.append("},\n");
        result.append("\"base\": \""+base+"\", \n");
        result.append("\"date\": \"2020-11-02\" \n");
        result.append("}");
        System.out.println(result.toString());
        return result.toString();
    }

    public static String creatApiDataResponse2(String base, Map<String, Double> rates) {
        StringBuilder result = new StringBuilder("{\n");
        appendKeyValueToStr1(result, "result", "\"success\"");
        appendKeyValueToStr1(result, "documentation", "\"success\"");
        appendKeyValueToStr1(result, "terms_of_use", "\"success\"");
        appendKeyValueToStr1(result, "time_last_update_unix", "\"1604361610\"");
        appendKeyValueToStr1(result, "time_last_update_utc", "\"Tue, 03 Nov 2020 00:00:10 +0000\"");
        appendKeyValueToStr1(result, "time_next_update_unix", "\"1604448130\"");
        appendKeyValueToStr1(result, "time_next_update_utc", "\"Wed, 04 Nov 2020 00:02:10 +0000\"");
        appendKeyValueToStr1(result, "base_code", "\""+base+"\"");
        appendNewLine(appendKeyValueToStr2(result, "conversion_rates", "{\n" + createStr(rates) + "\n}"));
        result.append("}\n");
        System.out.println(result.toString());
        return result.toString();
    }

    public static double randomRate() {
        return random().nextDouble(0, 101);
    }

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

    private static StringBuilder appendKeyValueToStr1(StringBuilder result, String key, String value) {
        return result.append("\"").append(key).append("\"").append(": ").append(value).append(",").append("\n");
    }

    private static StringBuilder appendKeyValueToStr2(StringBuilder result, String key, String value) {
        return result.append("\"").append(key).append("\"").append(": ").append(value);
    }

    public static StringBuilder appendComma(StringBuilder result)
    {
        return result.append(",");
    }

    public static StringBuilder appendNewLine(StringBuilder result)
    {
        return result.append("\n");
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
        result.append("\n");
        System.out.println(result.toString());
        return result.toString();
    }

    private static Map<String, Double> createRates(String to, double rate) {
        Map<String, Double> rates = new HashMap<String, Double>();
        rates.put(to, rate);
        return rates;
    }

    public static void setConfig(DynamicPropertyRegistry r, Boolean useShuffle, int cacheDuration) {
        r.add("exchange.cacheDuration", ()->cacheDuration);
        r.add("exchange.useShuffle", () -> useShuffle.toString());
        r.add("exchange.baseUrls[0]", () -> String.format("http://localhost:%s",8181));
        r.add("exchange.baseUrls[1]", () -> String.format("http://localhost:%s",7171));
    }

    public static void setShuffle(DynamicPropertyRegistry r, Boolean useShuffle) {
        r.add("exchange.useShuffle", () -> useShuffle.toString());
    }

    public static double currencyRound(double value) {
        int places =2;
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
