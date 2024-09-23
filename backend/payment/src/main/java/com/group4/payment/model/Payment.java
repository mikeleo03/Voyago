package com.group4.payment.model;

import com.group4.payment.auditor.AuditorBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Payment")
@EqualsAndHashCode(callSuper = true)
public class Payment extends AuditorBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Min(value = 0, message = "Nominal must be a positive number")
    @Column(name = "nominal", nullable = false)
    private int nominal;

    @NotBlank(message = "Picture is mandatory")
    @Column(name = "picture")
    private String picture;

    @NotBlank(message = "Payment date is mandatory")
    @Column(name = "paymentDate")
    private LocalDateTime paymentDate;

    @NotBlank(message = "Status is mandatory")
    @Column(name = "status", nullable = false)
    private String status;
}
