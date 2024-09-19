package com.group4.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.group4.gateway.client.AuthClient;

@Component
public class ApiKeyGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyGatewayFilterFactory.class);

    private final AuthClient authClient;

    public ApiKeyGatewayFilterFactory(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            log.info("Request Path: {} - {}", exchange.getRequest().getMethod(), path);

            // Skip preflight request - OPTIONS
            if (exchange.getRequest().getMethod().toString().equals("OPTIONS")) {
                log.info("kip preflight request - OPTIONS");
                return chain.filter(exchange);
            }

            String apiKey = exchange.getRequest().getHeaders().getFirst("api-key");
            log.info("Processing API Key: {}", apiKey);

            return authClient.isValidApiKey(apiKey)
                .flatMap(valid -> {
                    if (Boolean.TRUE.equals(valid)) {
                        log.info("API Key {} is valid. Proceeding with request.", apiKey);
                        return chain.filter(exchange);
                    } else {
                        log.warn("API Key {} is invalid. Unauthorized access attempt.", apiKey);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
        };
    }
}