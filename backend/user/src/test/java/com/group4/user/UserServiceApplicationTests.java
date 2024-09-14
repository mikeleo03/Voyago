package com.group4.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = UserServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
