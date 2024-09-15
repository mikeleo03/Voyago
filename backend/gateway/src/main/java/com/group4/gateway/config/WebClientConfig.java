package com.group4.gateway.config;

import com.group4.gateway.client.AuthClient;
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
    public AuthClient authClient(WebClient.Builder webClientBuilder) {
        return new AuthClient(webClientBuilder);
    }
}

