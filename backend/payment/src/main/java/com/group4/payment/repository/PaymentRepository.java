package com.group4.payment.repository;

import com.group4.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByPictureIsNullAndCreatedAtBeforeAndStatus(LocalDateTime createdAt, String status);
}
