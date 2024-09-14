package com.group4.tour;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = TourServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class TourServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
