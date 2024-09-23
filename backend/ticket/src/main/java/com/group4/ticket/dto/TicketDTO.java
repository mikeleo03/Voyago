package com.group4.ticket.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private String username;
    private String tourID;
    private String paymentID;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private boolean isRescheduled;
    private boolean isReviewed;
    private Set<TicketDetailDTO> ticketDetails;
}
