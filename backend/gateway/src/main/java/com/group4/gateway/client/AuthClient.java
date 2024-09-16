package com.group4.gateway.client;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.group4.gateway.exceptions.ResourceNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    private final WebClient.Builder webClientBuilder;
    private final EurekaClient eurekaClient;
    private static final String SERVICE_NAME = "authentication-service";

    @Autowired
    public AuthClient(EurekaClient eurekaClient, WebClient.Builder webClientBuilder) {
        this.eurekaClient = eurekaClient;
        this.webClientBuilder = webClientBuilder;
    }

    // Abstracted method to retrieve WebClient for the authentication service
    private WebClient getWebClientForAuthService() {
        // Retrieve instances of the authentication service
        List<InstanceInfo> instances = eurekaClient.getApplication(SERVICE_NAME).getInstances();

        // Handle the case where no instances are found
        if (instances.isEmpty()) {
            throw new ResourceNotFoundException("No instances of the authentication service are available.");
        }

        // Get the first available instance
        InstanceInfo service = instances.get(0);
        String hostName = service.getHostName();
        int port = service.getPort();

        // Construct the base URL for the WebClient
        URI url = URI.create("http://" + hostName + ":" + port + "/api/v1/auth");

        // Build and return the WebClient
        return webClientBuilder.baseUrl(url.toString()).build();
    }

    public Mono<Boolean> isValidApiKey(String apiKey) {
        log.debug("Sending request to validate API key: {}", apiKey);

        WebClient webClient = getWebClientForAuthService();
        
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

        WebClient webClient = getWebClientForAuthService();
        
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
