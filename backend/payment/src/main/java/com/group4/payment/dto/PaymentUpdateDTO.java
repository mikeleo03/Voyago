package com.group4.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentUpdateDTO {
    private String status;
    private String picture;
    private LocalDateTime paymentDate;
}