package com.group4.user.client;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.group4.user.dto.SignupRequest;
import com.group4.user.exceptions.ResourceNotFoundException;
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

    // Send some user data to AuthService after signup
    public Mono<Boolean> sendUpdateData(SignupRequest signupRequest) {
        log.debug("Sending request to the Auth Service: {}", signupRequest);

        WebClient webClient = getWebClientForAuthService();
        
        return webClient.post()
                .uri("/update")
                .body(Mono.just(signupRequest), SignupRequest.class)  // Passing the signupRequest as the request body
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(valid -> log.debug("Received response for signup request: {}", valid))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error sending signup request: {}. Status code: {}. Response body: {}",
                        signupRequest, ex.getStatusCode(), ex.getResponseBodyAsString());
                    if (ex.getStatusCode().is4xxClientError()) {
                        return Mono.just(false);
                    }
                    return Mono.error(ex);
                });
    }
}
