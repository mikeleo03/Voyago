package com.group4.gateway.client;

import com.group4.gateway.exceptions.ResourceNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthClientTest {

    @Mock
    private EurekaClient eurekaClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AuthClient authClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mocking the WebClient behavior
        when(webClientBuilder.baseUrl(any(String.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }


    @Test
    public void testIsValidApiKey_Success() {
        String apiKey = "valid-api-key";

        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setAppName("authentication-service")
                .setHostName("localhost")
                .setPort(8080)
                .build();

        when(eurekaClient.getApplication("authentication-service")).thenReturn(new com.netflix.discovery.shared.Application("authentication-service", List.of(instanceInfo)));

        when(webClientBuilder.baseUrl(any(String.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        Mono<Boolean> result = authClient.isValidApiKey(apiKey);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testValidateJwt_Success() {
        String token = "valid-token";

        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setAppName("authentication-service")
                .setHostName("localhost")
                .setPort(8080)
                .build();

        when(eurekaClient.getApplication("authentication-service")).thenReturn(new com.netflix.discovery.shared.Application("authentication-service", List.of(instanceInfo)));

        when(webClientBuilder.baseUrl(any(String.class))).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

        Mono<Boolean> result = authClient.validateJwt(token);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testGetWebClientForAuthService_NoInstances() {
        when(eurekaClient.getApplication("authentication-service")).thenReturn(new com.netflix.discovery.shared.Application("authentication-service", Collections.emptyList()));

        Mono<Boolean> result = Mono.defer(() -> {
            try {
                authClient.isValidApiKey("some-api-key").block();
                return Mono.just(true);
            } catch (ResourceNotFoundException ex) {
                return Mono.just(false);
            }
        });

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
