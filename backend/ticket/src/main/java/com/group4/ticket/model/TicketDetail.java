package com.group4.ticket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TicketDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketId")
    private Ticket ticket;

    private String name;

    @Column(length = 15)
    private String phone;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;
}
