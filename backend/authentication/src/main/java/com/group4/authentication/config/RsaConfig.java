package com.group4.authentication.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class RsaConfig {
    // No need to define a bean for RsaKeyConfigProperties
}
