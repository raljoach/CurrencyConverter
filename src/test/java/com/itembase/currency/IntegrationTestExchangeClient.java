package com.itembase.currency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTestExchangeClient {
    /* TODO: Integration Test cases for ExchangeClient:
        T1: payload type1 of API1 -> ExchangeData
        T2: payload type2 of API2 -> ExchangeData
        T3: webServer throws 400, \n" + 404, \n" + timeout
     */
    /*  Pseudocode:
             when(exchangeServer1.get(....)).thenReturn(
                     "{ \n" +
  "\"rates\": { \n" +
    "\"CAD\": 1.5466, \n" +
    \"HKD\": 9.0327, \n" +
    "\"ISK\": 163.5, \n" +
    "\"PHP\": 56.407, \n" +
    "\"DKK\": 7.4455, \n" +
    "\"HUF\": 366.24, \n" +
    "\"CZK\": 27.131, \n" +
    "\"AUD\": 1.6533, \n" +
    "\"RON\": 4.8674, \n" +
    "\"SEK\": 10.3625, \n" +
    "\"IDR\": 17064.82, \n" +
    "\"INR\": 86.7555, \n" +
    "\"BRL\": 6.6916, \n" +
    "\"RUB\": 93.745, \n" +
    "\"HRK\": 7.5695, \n" +
    "\"JPY\": 121.93, \n" +
    "\"THB\": 36.249, \n" +
    "\"CHF\": 1.0695, \n" +
    "\"SGD\": 1.5903, \n" +
    "\"PLN\": 4.6018, \n" +
    "\"BGN\": 1.9558, \n" +
    "\"TRY\": 9.8332, \n" +
    "\"CNY\": 7.7962, \n" +
    "\"NOK\": 11.1128, \n" +
    "\"NZD\": 1.7565, \n" +
    "\"ZAR\": 18.8972, \n" +
    "\"USD\": 1.1652, \n" +
    "\"MXN\": 24.7327, \n" +
    "\"ILS\": 3.9681, \n" +
    "\"GBP\": 0.90053, \n" +
    "\"KRW\": 1320.61, \n" +
    "\"MYR\": 4.8443
  }, \n" +
  "\"base\": \"EUR\", \n" +
  "\"date\": \"2020-11-02\" \n" +
"}"
);
     */
    @Test
    public void testGetRate_Api1_Data_Format(){
        assertEquals(1==2,"Not Implemented");
    }

    /* TODO: Integration Test cases for ExchangeClient:
    Pseudocode:
    https://api.exchangeratesapi.io/latest?symbols=USD,GBP

    when(exchangeServer1.get(....)).thenReturn(
                     "{ \n" +
{"rates":{"USD":1.1652,"GBP":0.90053},"base":"EUR","date":"2020-11-02"}
"}"
             );

     */
    @Test
    public void testGetRate_Api1_Data_Format2(){
        assertEquals(1==2,"Not Implemented");
    }

    /* TODO: Integration Test cases for ExchangeClient:
    Pseudocode:
             when(exchangeServer1.get(....)).thenReturn(
                     "{ \n" +
                       "result": "success",
  "documentation": "https://www.exchangerate-api.com/docs",
  "terms_of_use": "https://www.exchangerate-api.com/terms",
  "time_last_update_unix": 1604361610,
  "time_last_update_utc": "Tue, 03 Nov 2020 00:00:10 +0000",
  "time_next_update_unix": 1604448130,
  "time_next_update_utc": "Wed, 04 Nov 2020 00:02:10 +0000",
  "base_code": "USD",
  "conversion_rates": {
    "USD": 1,
    "AED": 3.672,
    "ARS": 78.288,
    "AUD": 1.4203,
    "BGN": 1.6791,
    "BRL": 5.7433,
    "BSD": 1,
    "CAD": 1.3275,
    "CHF": 0.9181,
    "CLP": 772.6863,
    "CNY": 6.6914,
    "COP": 3859.6,
    "CZK": 23.3309,
    "DKK": 6.3924,
    "DOP": 58.2142,
    "EGP": 15.6671,
    "EUR": 0.859,
    "FJD": 2.1418,
    "GBP": 0.7736,
    "GTQ": 7.7846,
    "HKD": 7.7525,
    "HRK": 6.4999,
    "HUF": 314.692,
    "IDR": 14770.1809,
    "ILS": 3.4052,
    "INR": 74.4028,
    "ISK": 140.5768,
    "JPY": 104.7077,
    "KRW": 1134.1153,
    "KZT": 431.2402,
    "MVR": 15.41,
    "MXN": 21.295,
    "MYR": 4.1564,
    "NOK": 9.5501,
    "NZD": 1.5095,
    "PAB": 1,
    "PEN": 3.6139,
    "PHP": 48.4087,
    "PKR": 159.4876,
    "PLN": 3.9542,
    "PYG": 7017.4545,
    "RON": 4.1766,
    "RUB": 80.3098,
    "SAR": 3.7501,
    "SEK": 8.9006,
    "SGD": 1.3655,
    "THB": 31.1343,
    "TRY": 8.4164,
    "TWD": 28.5732,
    "UAH": 28.507,
    "UYU": 42.8844,
    "ZAR": 16.2391
  }

                     "}"
             );
*/
    @Test
    public void testGetRate_Api2_Data_Format(){
        assertEquals(1==2,"Not Implemented");
    }
}
