package com.group4.payment.service;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.model.Payment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PaymentService {
    Payment getPaymentById(String id);
    Payment createPayment(PaymentCreateDTO dto);
    Payment changeVerifyStatus(String id, String status);
    Payment addPaymentEvidence(String id, PaymentUpdateDTO dto);
    String saveImage(MultipartFile file);
}
