package com.group4.authentication.config;

import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SpringBootTest
class RsaConfigTest {

    @Test
    void contextLoads() {
        // Just check that the context loads correctly
        RsaConfig rsaConfig = new RsaConfig();
        assertThat(rsaConfig).isNotNull();
    }
}

