package com.group4.ticket.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group4.ticket.data.model.TicketDetail;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail, String> {
}
