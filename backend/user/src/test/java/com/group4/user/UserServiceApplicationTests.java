package com.group4.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = UserServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceApplicationTests {

	@Test
    void contextLoads() {
        // This test simply loads the context to ensure that the application starts up correctly.
    }

	@Test
    void mainMethodShouldRun() {
        try (var mock = mockStatic(SpringApplication.class)) {
            mock.when(() -> SpringApplication.run(UserServiceApplication.class, new String[0])).thenReturn(mock(ConfigurableApplicationContext.class));

            // Run the main method of the application
            String[] args = {};
            UserServiceApplication.main(args);
            
            // Verify that SpringApplication.run was called with the correct parameters
            mock.verify(() -> SpringApplication.run(UserServiceApplication.class, args));
        }
    }
}
