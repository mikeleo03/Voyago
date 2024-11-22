package com.group4.ticket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = TicketServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class TicketServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
