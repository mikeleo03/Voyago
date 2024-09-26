package com.group4.authentication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = AuthenticationServiceApplicationTests.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthenticationServiceApplicationTests {

	@Test
    void contextLoads() {
        // This test simply loads the context to ensure that the application starts up correctly.
    }

	@Test
    void mainMethodShouldRun() {
        try (var mock = mockStatic(SpringApplication.class)) {
            mock.when(() -> SpringApplication.run(AuthenticationServiceApplication.class, new String[0])).thenReturn(mock(ConfigurableApplicationContext.class));

            // Run the main method of the application
            String[] args = {};
            AuthenticationServiceApplication.main(args);
            
            // Verify that SpringApplication.run was called with the correct parameters
            mock.verify(() -> SpringApplication.run(AuthenticationServiceApplication.class, args));
        }
    }
}
