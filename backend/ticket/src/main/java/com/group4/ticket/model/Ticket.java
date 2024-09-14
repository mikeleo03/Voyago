package com.group4.ticket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(length = 36)
    private String userID;

    @Column(length = 36)
    private String tourID;

    @Column(length = 36)
    private String paymentID;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isRescheduled;

    private boolean isReviewed;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private Set<TicketDetail> ticketDetails;
}
