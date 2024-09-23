package com.group4.payment.service.impl;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.exception.FileNotFoundException;
import com.group4.payment.exception.ResourceNotFoundException;
import com.group4.payment.exception.TimeOutException;
import com.group4.payment.mapper.PaymentMapper;
import com.group4.payment.model.Payment;
import com.group4.payment.repository.PaymentRepository;
import com.group4.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final String uploadDir = "src/main/resources/static/assets/";

    @Override
    public Payment getPaymentById(String id){
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("Payment not found for this id : " + id);
        }
        return paymentOptional.get();
    }

    @Override
    public Payment createPayment(PaymentCreateDTO dto) {
        Payment payment = paymentMapper.toPayment(dto);
        payment.setStatus("UNVERIFIED");
        payment.setPicture(null);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment changeVerifyStatus(String id, String status) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("Payment not found for this id : " + id);
        }
        Payment payment = paymentOptional.get();
        if (Objects.equals(payment.getStatus(), "FAILED")){
            throw new TimeOutException("Payment already failed.");
        }
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment addPaymentEvidence(String id, MultipartFile file) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()){
            throw new ResourceNotFoundException("Payment not found for this id : " + id);
        }
        Payment payment = paymentOptional.get();
        if (Objects.equals(payment.getStatus(), "FAILED")){
            throw new TimeOutException("Payment already failed.");
        }
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveImage(file);
            payment.setPicture(imageUrl);
        } else{
            throw new FileNotFoundException("File not found.");
        }
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public String saveImage(MultipartFile file){
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        try {
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save image: " + e.getMessage());
        }
    }
}
