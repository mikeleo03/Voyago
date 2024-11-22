package com.group4.ticket.client;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.group4.ticket.data.model.Payment;
import com.group4.ticket.dto.PaymentCreateDTO;
import com.group4.ticket.exceptions.ResourceNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import reactor.core.publisher.Mono;

@Component
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);

    private final WebClient.Builder webClientBuilder;
    private final EurekaClient eurekaClient;
    private static final String PAYMENT_SERVICE = "payment-service";

    @Autowired
    public PaymentClient(EurekaClient eurekaClient, WebClient.Builder webClientBuilder) {
        this.eurekaClient = eurekaClient;
        this.webClientBuilder = webClientBuilder;
    }

    // Abstracted method to retrieve WebClient for the payment service
    private WebClient getWebClientForPaymentService() {
        // Retrieve instances of the payment service
        List<InstanceInfo> instances = eurekaClient.getApplication(PAYMENT_SERVICE).getInstances();

        // Handle the case where no instances are found
        if (instances.isEmpty()) {
            throw new ResourceNotFoundException("No instances of the payment service are available.");
        }

        // Get the first available instance
        InstanceInfo service = instances.get(0);
        String hostName = service.getHostName();
        int port = service.getPort();

        // Construct the base URL for the WebClient
        URI url = URI.create("http://" + hostName + ":" + port + "/api/v1/payment");

        // Build and return the WebClient
        return webClientBuilder.baseUrl(url.toString()).build();
    }

    private <T> Mono<T> handleError(Mono<T> mono, String paymentID) {
        return mono.onErrorResume(WebClientResponseException.class, ex -> {
            log.error("Error fetching payment details for Payment ID {}: Status code: {}, Response body: {}",
                    paymentID, ex.getStatusCode(), ex.getResponseBodyAsString());
            if (ex.getStatusCode().is4xxClientError()) {
                return Mono.error(new ResourceNotFoundException("Payment not found for ID: " + paymentID));
            }
            return Mono.error(ex);
        });
    }

    // Method to create payment using a nominal value and retrieve only the payment ID
    public Mono<String> createPayment(Integer nominal) {
        WebClient webClient = getWebClientForPaymentService();

        return handleError(webClient.post()
                .uri("")
                .bodyValue(new PaymentCreateDTO(nominal))
                .retrieve()
                .bodyToMono(Payment.class)
                .map(Payment::getId), String.valueOf(nominal));
    }
}