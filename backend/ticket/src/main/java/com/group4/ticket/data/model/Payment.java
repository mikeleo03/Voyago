package com.group4.ticket.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private int nominal;
    private String picture;
    private LocalDateTime paymentDate;
    private String status;
}
