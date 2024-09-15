package com.group4.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.group4.gateway.client.AuthClient;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthGatewayFilterFactory.class);
    
    private final AuthClient authClient;

    public JwtAuthGatewayFilterFactory(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            logger.info("Request Path: {}", path);

            // Skip JWT validation for specific paths
            if ("/api/v1/auth/login".equals(path) || "/api/v1/auth/signup".equals(path)) {
                logger.info("Skipping JWT validation for path: {}", path);
                return chain.filter(exchange);
            }

            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            logger.info("Validating JWT token: {}", token);

            return authClient.validateJwt(token)
                .flatMap(valid -> {
                    if (Boolean.TRUE.equals(valid)) {
                        logger.info("JWT validation successful for token");
                        return chain.filter(exchange);
                    } else {
                        logger.warn("JWT validation failed for token");
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .doOnError(ex -> logger.error("Error during JWT validation", ex));
        };
    }
}