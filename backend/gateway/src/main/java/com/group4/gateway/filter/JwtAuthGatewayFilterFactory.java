package com.group4.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.group4.gateway.client.AuthClient;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthGatewayFilterFactory.class);

    private final AuthClient authClient;

    // List of exempted paths
    private static final List<String> EXEMPTED_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/users/signup",
            "/api/v1/users/email",
            "/api/v1/users/send"
    );

    // Regular expression for dynamic paths (e.g., /api/v1/users/{id}/password)
    private static final Pattern USER_PASSWORD_PATH_PATTERN = Pattern.compile("/api/v1/users/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/password$");

    public JwtAuthGatewayFilterFactory(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            logger.info("Request Path: {} - {}", exchange.getRequest().getMethod(), path);

            // Skip preflight request - OPTIONS
            if ("OPTIONS".equals(exchange.getRequest().getMethod().toString())) {
                logger.info("Skip preflight request - OPTIONS");
                return chain.filter(exchange);
            }

            // Check if the path is in exempted paths or matches the dynamic user password path
            if (EXEMPTED_PATHS.contains(path) || USER_PASSWORD_PATH_PATTERN.matcher(path).matches()) {
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
            String finalToken = token;
            return authClient.validateJwt(finalToken)
                .flatMap(valid -> {
                    if (Boolean.TRUE.equals(valid)) {
                        logger.info("JWT validation successful for token");

                        // Forward the validated JWT to the downstream service
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