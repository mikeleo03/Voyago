package com.group4.ticket.client;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.group4.ticket.data.model.Tour;
import com.group4.ticket.exceptions.ResourceNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import reactor.core.publisher.Mono;

@Component
public class TourClient {

    private static final Logger log = LoggerFactory.getLogger(TourClient.class);

    private final WebClient.Builder webClientBuilder;
    private final EurekaClient eurekaClient;
    private static final String TOUR_SERVICE = "tour-service";

    @Autowired
    public TourClient(EurekaClient eurekaClient, WebClient.Builder webClientBuilder) {
        this.eurekaClient = eurekaClient;
        this.webClientBuilder = webClientBuilder;
    }

    // Abstracted method to retrieve WebClient for the tour service
    private WebClient getWebClientForTourService() {
        // Retrieve instances of the tour service
        List<InstanceInfo> instances = eurekaClient.getApplication(TOUR_SERVICE).getInstances();

        // Handle the case where no instances are found
        if (instances.isEmpty()) {
            throw new ResourceNotFoundException("No instances of the tour service are available.");
        }

        // Get the first available instance
        InstanceInfo service = instances.get(0);
        String hostName = service.getHostName();
        int port = service.getPort();

        // Construct the base URL for the WebClient
        URI url = URI.create("http://" + hostName + ":" + port + "/api/v1/tour");

        // Build and return the WebClient
        return webClientBuilder.baseUrl(url.toString()).build();
    }

    private <T> Mono<T> handleError(Mono<T> mono, String tourID) {
        return mono.onErrorResume(WebClientResponseException.class, ex -> {
            log.error("Error fetching tour details for Tour ID {}: Status code: {}, Response body: {}",
                    tourID, ex.getStatusCode(), ex.getResponseBodyAsString());
            if (ex.getStatusCode().is4xxClientError()) {
                return Mono.error(new ResourceNotFoundException("Tour not found for ID: " + tourID));
            }
            return Mono.error(ex);
        });
    }

    // Method to fetch tour details using tourID
    public Mono<String> getTourNameById(String tourID) {
        WebClient webClient = getWebClientForTourService();

        return handleError(webClient.get()
                .uri("/{id}", tourID)
                .retrieve()
                .bodyToMono(Tour.class)
                .map(Tour::getTitle), tourID);
    }

    // Method to fetch tour details using tourID
    public Mono<Integer> getTourPriceById(String tourID) {
        WebClient webClient = getWebClientForTourService();

        return handleError(webClient.get()
                .uri("/{id}", tourID)
                .retrieve()
                .bodyToMono(Tour.class)
                .map(Tour::getPrices), tourID);
    }

    // Method to fetch tour details using tourID
    public Mono<Void> updateTourQuantityById(String tourID, Integer quantity) {
        WebClient webClient = getWebClientForTourService();

        return handleError(webClient.put()
                .uri(uriBuilder -> uriBuilder
                    .path("/reduce")
                    .queryParam("id", tourID)
                    .queryParam("quantity", quantity)
                    .build())
                .retrieve()
                .bodyToMono(Void.class), tourID);
    }
}
