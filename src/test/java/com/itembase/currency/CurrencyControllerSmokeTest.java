package com.itembase.currency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CurrencyControllerSmokeTest {
	@Autowired
	private CurrencyController controller;

	// Verifies controller is being created by MainApplication
	@Test
	public void testContextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}


}
