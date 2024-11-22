package com.group4.payment.service;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.model.Payment;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    Payment getPaymentById(String id);
    Payment createPayment(PaymentCreateDTO dto);
    Payment changeVerifyStatus(String id, String status);
    Payment addPaymentEvidence(String id, MultipartFile file);
    String saveImage(MultipartFile file);
}
