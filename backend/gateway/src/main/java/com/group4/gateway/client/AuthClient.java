package com.group4.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    private final WebClient webClient;

    @Autowired
    public AuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/v1/auth").build();
    }

    public Mono<Boolean> isValidApiKey(String apiKey) {
        log.debug("Sending request to validate API key: {}", apiKey);
        return webClient.get()
                .uri("/validate?key=" + apiKey)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(valid -> log.debug("Received response for API key validation: {}", valid))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error validating API key: {}. Status code: {}. Response body: {}", 
                              apiKey, ex.getStatusCode(), ex.getResponseBodyAsString());
                    if (ex.getStatusCode().is4xxClientError()) {
                        return Mono.just(false);
                    }
                    return Mono.error(ex);
                });
    }

    public Mono<Boolean> validateJwt(String token) {
        log.debug("Sending request to validate JWT token: {}", token);
        return webClient.get()
                .uri("/validateToken?token=" + token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(valid -> log.debug("Received response for JWT validation: {}", valid))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error validating JWT token: {}. Status code: {}. Response body: {}",
                              token, ex.getStatusCode(), ex.getResponseBodyAsString());
                    if (ex.getStatusCode().is4xxClientError()) {
                        return Mono.just(false);
                    }
                    return Mono.error(ex);
                });
    }
}