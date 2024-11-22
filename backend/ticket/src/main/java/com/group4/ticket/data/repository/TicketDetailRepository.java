package com.group4.ticket.data.repository;

import com.group4.ticket.data.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group4.ticket.data.model.TicketDetail;
import com.group4.ticket.data.model.TicketDetailKey;

import java.util.Set;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail, TicketDetailKey> {
    Set<TicketDetail> findByTicket(Ticket ticket);
}
