package com.itembase.currency;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CurrencyController {

    @GetMapping("/hello")
    public String sayHello(
            @RequestParam(value = "name", defaultValue = "World") String name,
            Model model) {
        model.addAttribute("user", name);
        return "hello " + name;
    }

/*
    @GetMapping
    public String convert() throws Exception {

        String baseUrl = "https://api.exchangeratesapi.io", rateUrl="/latest?base=";
        String baseUrl2 = "https://v6.exchangerate-api.com/v6/68931214b500a7a0f882192f", rateUrl2="/latest/";
        List<Object> clients = new ArrayList<Object>();
        clients.add(new ExchangeGenericApiClient<ExchangeData>(baseUrl,rateUrl));
        clients.add(new ExchangeGenericApiClient<ExchangeData2>(baseUrl2,rateUrl2));

        var html = "<html><body><h1>Currency Conversion Example</h1><br><br>";

        for(int i=0; i<clients.size(); i++)
        {
            html +=
            (((ExchangeGenericApiClient<IExchangeData>) clients.get(i))).getExchangeData("EUR")+
                    "<br>";

        }

        html+="</body></html>";
        return html;
    }
*/

}
