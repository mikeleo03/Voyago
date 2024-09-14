package com.group4.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = AuthenticationServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthenticationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
