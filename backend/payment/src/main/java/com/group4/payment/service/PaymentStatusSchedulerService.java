package com.group4.payment.service;

import com.group4.payment.model.Payment;
import com.group4.payment.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentStatusSchedulerService {
    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 1000)
    public void updateUnverifiedPayments() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        List<Payment> unverifiedPayments = paymentRepository.findByCreatedAtBeforeAndStatus(thirtyMinutesAgo, "UNVERIFIED");

        for (Payment payment : unverifiedPayments) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }
    }
}
