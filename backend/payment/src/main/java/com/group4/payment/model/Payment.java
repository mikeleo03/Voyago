package com.group4.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private int nominal;

    private String picture;

    private LocalDateTime paymentDate;

    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;
}
