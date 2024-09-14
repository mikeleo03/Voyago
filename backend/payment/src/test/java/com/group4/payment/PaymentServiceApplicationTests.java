package com.group4.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PaymentServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class PaymentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
