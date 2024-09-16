package com.group4.gateway.config;

import com.group4.gateway.client.AuthClient;
import com.netflix.discovery.EurekaClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public AuthClient authClient(EurekaClient eurekaClient, WebClient.Builder webClientBuilder) {
        return new AuthClient(eurekaClient, webClientBuilder);
    }
}

