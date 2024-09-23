package com.group4.payment.controller;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.mapper.PaymentMapper;
import com.group4.payment.model.Payment;
import com.group4.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentCreateDTO dto) {
        Payment payment = paymentService.createPayment(dto);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> changeVerifyStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok().body(paymentService.changeVerifyStatus(id, status));
    }

    @PutMapping("/evidence/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> addPaymentEvidence(@PathVariable String id, @RequestBody PaymentUpdateDTO dto) {
        return ResponseEntity.ok().body(paymentService.addPaymentEvidence(id, dto));
    }
}
