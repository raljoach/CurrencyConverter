package com.itembase.currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTestExchangeData {
    /* Test cases:
        [X] T1: valid to: "eur "," eUr ", " EUR"
        [X] T2: invalid to: EURX
        [X] T3: wrong to: @!-+
        [X] T4: non-existent to: GGG
        T5: rates empty
        T6: rates null
     */
    private static Map<String, Double> rates = new HashMap<String, Double>();
    private static String[] currencies;

    @BeforeAll
    private static void setup() {
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

    @Test
    public void testFindRate() {
        // arrange
        var payload = new ExchangeData();
        payload.setRates(rates);
        Optional<String> res = Arrays.stream(currencies).findAny();
        String to = res.get();
        double expectedRate = rates.get(to);

        // act
        var actualRate1 = payload.findRate(to);
        var actualRate2 = payload.findRate(to.toLowerCase());
        var actualRate3 = payload.findRate(to.toUpperCase());
        var randomCaseStr = randomUpperCase(to.toLowerCase());
        var actualRate4 = payload.findRate(randomCaseStr + " ");
        var actualRate5 = payload.findRate(" " + randomCaseStr + " ");
        var actualRate6 = payload.findRate(" " + randomCaseStr);

        // assert
        assertEquals(expectedRate, actualRate1);
        assertEquals(expectedRate, actualRate2);
        assertEquals(expectedRate, actualRate3);
        assertEquals(expectedRate, actualRate4);
        assertEquals(expectedRate, actualRate5);
        assertEquals(expectedRate, actualRate6);
    }

    @Test
    public void testFindRate_To_Invalid()
    {
        // arrange
        var payload = new ExchangeData();
        payload.setRates(rates);
        Optional<String> res = Arrays.stream(currencies).findAny();
        String to1 = "-"+res.get()+"-";
        String to2 = "@!/+";

        // act
        Assertions.assertThrows(ApiException.class,()->payload.findRate(to1), "To not found: " + to1);
        Assertions.assertThrows(ApiException.class,()->payload.findRate(to2), "To not found: " + to2);
    }

    @Test
    public void testFindRate_To_NonExistent()
    {
        // arrange
        var payload = new ExchangeData();
        String to = "EUR";
        double expectedRate = rates.get(to);
        rates.remove(to);
        payload.setRates(rates);

        try {
            // act
            Assertions.assertThrows(ApiException.class, () -> payload.findRate(to), "To not found: " + to);
        }
        finally{
            rates.put(to, expectedRate);
        }
    }

    private static double randomRate() {
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
    private static String randomUpperCase(String lowerCaseStr) {
        var length = lowerCaseStr.length();
        if(length==0) return lowerCaseStr;
        var index = random().nextInt(0, length);
        Character ch = lowerCaseStr.charAt(index);
        Character upperChar = ch.toString().toUpperCase().charAt(0);

        return lowerCaseStr.substring(0, index).concat(upperChar.toString()).concat(lowerCaseStr.substring(index+1));
    }

    private static ThreadLocalRandom random(){
        return ThreadLocalRandom.current();
    }
}
