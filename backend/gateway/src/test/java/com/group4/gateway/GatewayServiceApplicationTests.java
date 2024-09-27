package com.group4.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = GatewayServiceApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class GatewayServiceApplicationTests {

	@Test
	void contextLoads() {
		// This test simply loads the context to ensure that the application starts up correctly.
	}

	@Test
	void mainMethodShouldRun() {
		try (var mock = mockStatic(SpringApplication.class)) {
			mock.when(() -> SpringApplication.run(GatewayServiceApplication.class, new String[0])).thenReturn(mock(ConfigurableApplicationContext.class));

			// Run the main method of the application
			String[] args = {};
			GatewayServiceApplication.main(args);

			// Verify that SpringApplication.run was called with the correct parameters
			mock.verify(() -> SpringApplication.run(GatewayServiceApplication.class, args));
		}
	}
}
