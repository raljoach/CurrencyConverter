package com.itembase.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MainApplication.class, args);
		test();
	}

	private static void test()
	{
		var client = new ExchangeApiClient1();
		System.out.println("BEFORE");
		//System.out.print(client.getExchangeData2("EUR"));
		//client.printRates("EUR");
		System.out.print(client.outputRate("EUR", "USD"));
		System.out.println("\nAFTER");
	}

	private static void useGenericApiClient()
	{
		String baseUrl = "https://api.exchangeratesapi.io", rateUrl="/latest?base=";
		String baseUrl2 = "https://v6.exchangerate-api.com/v6/68931214b500a7a0f882192f", rateUrl2="/latest/";
		List<Object> clients = new ArrayList<Object>();
		clients.add(new ExchangeGenericApiClient<ExchangeData>(baseUrl,rateUrl));
		//clients.add(new ExchangeApiClient<ExchangeData2>(baseUrl2,rateUrl2));
		for(int i=0; i<clients.size(); i++)
		{
			System.out.println((((ExchangeGenericApiClient<IExchangeData>) clients.get(i))).getExchangeData("EUR"));
		}
	}

}
