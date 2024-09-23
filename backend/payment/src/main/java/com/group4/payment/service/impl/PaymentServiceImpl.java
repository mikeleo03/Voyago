package com.group4.payment.service.impl;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.exception.ResourceNotFoundException;
import com.group4.payment.mapper.PaymentMapper;
import com.group4.payment.model.Payment;
import com.group4.payment.repository.PaymentRepository;
import com.group4.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment createPayment(PaymentCreateDTO dto) {
        Payment payment = paymentMapper.toPayment(dto);
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Payment changeVerifyStatus(String id, String status) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("Payment not found for this id : " + id);
        }
        Payment payment = paymentOptional.get();
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment addPaymentEvidence(String id, PaymentUpdateDTO dto) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("Payment not found for this id : " + id);
        }
        Payment payment = paymentOptional.get();
        payment.setPicture(dto.getPicture());
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}
