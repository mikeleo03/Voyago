package com.group4.ticket.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.group4.ticket.data.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    // Find user data by its paymentID
    Optional<Ticket> findByPaymentID(String paymentID);
}

