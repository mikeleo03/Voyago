package com.group4.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(List.of("http://localhost:4200"));
        corsConfig.setAllowedMethods(List.of("PUT", "GET", "POST", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("Content-Type: Application/json", "Authorization"));
        corsConfig.setAllowCredentials(true);
        return new CorsWebFilter(exchange -> corsConfig);
    }
}