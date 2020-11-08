package com.itembase.currency;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @GetMapping(value="/sample", produces = "application/json")
    public Double sample() {
        var client = new ExchangeApiClient1();
        return client.getRate("EUR", "USD");
    }

    @PostMapping(value="/convert", produces = "application/json")
    public ConversionResponse convert(
            // TODO: Would be call to passing in media stream to test asynchronous response in web browser
            //@RequestHeader(value = "Accept", defaultValue="application/json") String acceptHeader,
            @RequestBody ConversionRequest convertRequest) {
        return doConversion(convertRequest);
    }

    @PostMapping(value="/convert2", produces = "application/json")
    public ConversionResponse convert2(
            // TODO: Would be call to passing in media stream to test asynchronous response in web browser
            //@RequestHeader(value = "Accept", defaultValue="application/json") String acceptHeader,
            @RequestBody ConversionRequest convertRequest) {
        return doConversion2(convertRequest);
    }

    @GetMapping("/convertDebug")
    public ConversionResponse convertDebug(
            @RequestParam(value = "from", defaultValue = "EUR") String from,
            @RequestParam(value = "to", defaultValue = "USD") String to,
            @RequestParam(value = "amount", defaultValue = "0") double amount) {
        var convertRequest = new ConversionRequest();
        convertRequest.setAmount(amount);
        convertRequest.setTo(to);
        convertRequest.setFrom(from);
        return doConversion(convertRequest);
    }

    @GetMapping("/convertDebug2")
    public ConversionResponse convertDebug2(
            @RequestParam(value = "from", defaultValue = "EUR") String from,
            @RequestParam(value = "to", defaultValue = "USD") String to,
            @RequestParam(value = "amount", defaultValue = "0") double amount) {
        var convertRequest = new ConversionRequest();
        convertRequest.setAmount(amount);
        convertRequest.setTo(to);
        convertRequest.setFrom(from);
        return doConversion2(convertRequest);
    }

    private ConversionResponse doConversion(ConversionRequest convertRequest) {
        var client = new ExchangeApiClient1();
        var rate = client.getRate(convertRequest.getFrom(), convertRequest.getTo());
        var converted = convertRequest.getAmount() * rate;
        var data = convertRequest.getData();
        data.setConverted(converted);
        return new ConversionResponse(data);
    }

    private ConversionResponse doConversion2(ConversionRequest convertRequest) {
        var client = new ExchangeApiClient2();
        var rate = client.getRate(convertRequest.getFrom(), convertRequest.getTo());
        var converted = convertRequest.getAmount() * rate;
        var data = convertRequest.getData();
        data.setConverted(converted);
        return new ConversionResponse(data);
    }

/*
    @GetMapping("/hello")
    public String sayHello(
            @RequestParam(value = "name", defaultValue = "World") String name,
            Model model) {
        model.addAttribute("user", name);
        return "hello " + name;
    }


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
