package com.group4.payment.service;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.model.Payment;

import java.util.Optional;

public interface PaymentService {
    Payment createPayment(PaymentCreateDTO dto);
    Payment changeVerifyStatus(String id, String status);
    Payment addPaymentEvidence(String id, PaymentUpdateDTO dto);
}
