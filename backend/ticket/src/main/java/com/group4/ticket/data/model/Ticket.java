package com.group4.ticket.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group4.ticket.auditor.AuditorBase;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Ticket")
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class Ticket extends AuditorBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Usernameis required")
    @Column(name = "username", nullable = false)
    private String username;

    @NotBlank(message = "Tour ID is required")
    @Column(name = "tourID", length = 36, nullable = false)
    private String tourID;

    @NotBlank(message = "Payment ID is required")
    @Column(name = "paymentID", length = 36, nullable = false)
    private String paymentID;

    @Column(name = "status", nullable = false)
    private String status = Status.UNUSED.toString();

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "isRescheduled", nullable = false)
    private boolean isRescheduled;

    @Column(name = "isReviewed", nullable = false)
    private boolean isReviewed;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TicketDetail> ticketDetails;
}