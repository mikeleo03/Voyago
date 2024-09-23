package com.group4.ticket.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketSaveDTO {
    private String userID;
    private String tourID;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<TicketDetailSaveDTO> ticketDetails;
}
