package com.itembase.currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTestExchangeData {

    @Test
    public void testFindRate() {
        // arrange
        var payload = new ExchangeData();
        payload.setRates(TestUtils.getRates());
        Optional<String> res = Arrays.stream(TestUtils.getCurrencies()).findAny();
        String to = res.get();
        double expectedRate = TestUtils.getRates().get(to);

        // act
        var actualRate1 = payload.findRate(to);
        var actualRate2 = payload.findRate(to.toLowerCase());
        var actualRate3 = payload.findRate(to.toUpperCase());
        var randomCaseStr = TestUtils.randomUpperCase(to.toLowerCase());
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
        payload.setRates(TestUtils.getRates());
        Optional<String> res = Arrays.stream(TestUtils.getCurrencies()).findAny();
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
        var rates = TestUtils.getRates();
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
}
