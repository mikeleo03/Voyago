package com.group4.payment.client;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.group4.payment.exceptions.ResourceNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import reactor.core.publisher.Mono;

@Component
public class TicketClient {

    private static final Logger log = LoggerFactory.getLogger(TicketClient.class);

    private final WebClient.Builder webClientBuilder;
    private final EurekaClient eurekaClient;
    private static final String TICKET_SERVICE = "ticket-service";

    @Autowired
    public TicketClient(EurekaClient eurekaClient, WebClient.Builder webClientBuilder) {
        this.eurekaClient = eurekaClient;
        this.webClientBuilder = webClientBuilder;
    }

    // Abstracted method to retrieve WebClient for the ticket service
    private WebClient getWebClientForTicketService() {
        // Retrieve instances of the ticket service
        List<InstanceInfo> instances = eurekaClient.getApplication(TICKET_SERVICE).getInstances();

        // Handle the case where no instances are found
        if (instances.isEmpty()) {
            throw new ResourceNotFoundException("No instances of the ticket service are available.");
        }

        // Get the first available instance
        InstanceInfo service = instances.get(0);
        String hostName = service.getHostName();
        int port = service.getPort();

        // Construct the base URL for the WebClient
        URI url = URI.create("http://" + hostName + ":" + port + "/api/v1/ticket");

        // Build and return the WebClient
        return webClientBuilder.baseUrl(url.toString()).build();
    }

    private <T> Mono<T> handleError(Mono<T> mono, String ticketID) {
        return mono.onErrorResume(WebClientResponseException.class, ex -> {
            log.error("Error fetching ticket details for Payment ID {}: Status code: {}, Response body: {}",
                    ticketID, ex.getStatusCode(), ex.getResponseBodyAsString());
            if (ex.getStatusCode().is4xxClientError()) {
                return Mono.error(new ResourceNotFoundException("Payment not found for ID: " + ticketID));
            }
            return Mono.error(ex);
        });
    }

    // Method to return the quantity back
    public Mono<Void> updateTourQuantityByPaymentId(String paymentID) {
        WebClient webClient = getWebClientForTicketService();

        return handleError(webClient.put()
                .uri(uriBuilder -> uriBuilder
                    .path("/return")
                    .queryParam("paymentId", paymentID)
                    .build())
                .retrieve()
                .bodyToMono(Void.class), paymentID);
    }
}