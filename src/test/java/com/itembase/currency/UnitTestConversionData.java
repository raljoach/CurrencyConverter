package com.itembase.currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class UnitTestConversionData {
    @Test
    void testConvert_Amount_BadInput() {
        // arrange inputs
        // TODO: Use Random values for inputs i.e. RandomUtils
        String from = "EUR";
        String to = "USD";
        double amount = -40;

        // assert
        Assertions.assertThrows(RuntimeException.class, ()->TestUtils.createConversionRequest(from, to, amount));
    }

}
