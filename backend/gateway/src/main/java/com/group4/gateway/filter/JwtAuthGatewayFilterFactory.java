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
            logger.info("Request Path: {} - {}", exchange.getRequest().getMethod(), path);

            // Skip preflight request - OPTIONS
            if (exchange.getRequest().getMethod().toString().equals("OPTIONS")) {
                logger.info("Skip preflight request - OPTIONS");
                return chain.filter(exchange);
            }

            // Skip JWT validation for specific paths
            if ("/api/v1/auth/login".equals(path) || "/api/v1/users/signup".equals(path) || "/api/v1/users/email".equals(path) || "/api/v1/users/send".equals(path)) {
                logger.info("Skipping JWT validation for path: {}", path);
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String token = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Strip "Bearer " from the token
            }

            logger.info("Validating JWT token: {}", token);

            // Validate the JWT token with the AuthClient
            String finalToken = token; // Define a final or effectively final variable to pass into the lambda
            return authClient.validateJwt(finalToken)
                .flatMap(valid -> {
                    if (Boolean.TRUE.equals(valid)) {
                        logger.info("JWT validation successful for token");

                        // Forward the validated JWT to the downstream service
                        // Add the Authorization header with the Bearer token
                        return chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                .header("Authorization", "Bearer " + finalToken)
                                .build())
                            .build());
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