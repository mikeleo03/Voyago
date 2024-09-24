package com.group4.payment.controller;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.model.Payment;
import com.group4.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN', 'CUSTOMER')"))
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id){
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Resource> getPaymentImage(@PathVariable String id) throws MalformedURLException {
        Payment payment = paymentService.getPaymentById(id);
        String imageName = payment.getPicture();

        Path path = Paths.get("src/main/resources/static/assets/" + imageName);

        if (Files.exists(path)) {
            Resource resource = new UrlResource(path.toUri());
            String contentType;
            try {
                contentType = Files.probeContentType(path);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } catch (IOException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentCreateDTO dto) {
        Payment payment = paymentService.createPayment(dto);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> changeVerifyStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok().body(paymentService.changeVerifyStatus(id, status));
    }

    @PutMapping("/payment/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> addPaymentEvidence(
            @PathVariable String id,
            @RequestPart(value = "file", required = true) MultipartFile file) {
        Payment payment = paymentService.addPaymentEvidence(id, file);
        return ResponseEntity.ok().body(payment);
    }
}
