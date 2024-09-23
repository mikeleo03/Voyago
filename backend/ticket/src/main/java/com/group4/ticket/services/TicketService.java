package com.group4.ticket.services;

import java.time.LocalDate;
import java.util.List;

import com.group4.ticket.data.model.Ticket;
import com.group4.ticket.dto.TicketDTO;
import com.group4.ticket.dto.TicketSaveDTO;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

public interface TicketService {

    // [Admin] Show list of tickets (for admin, show all)
    Page<Ticket> getAllTickets(Integer minPrice, Integer maxPrice, String sortPrice, LocalDate startDate, LocalDate endDate, int page, int size);

    // [Customer] Show list of tickets (for user history)
    Page<Ticket> getAllTicketsByUserID(String userID, Integer minPrice, Integer maxPrice, LocalDate startDate, LocalDate endDate, int page, int size);

    // [Customer] Show ticket by ID
    TicketDTO getTicketById(String id);

    // [Customer] Create new ticket
    TicketDTO createTicket(@Valid TicketSaveDTO ticketSaveDTO);

    // [-] Add payment evidence
    TicketDTO editPayment(String id, String paymentID);

    // [Admin] Change ticket status (used, unused)
    TicketDTO updateTicketStatus(String id, String status);

    // [Admin] Export list of tickets to Excel (for admin recap)
    // [Customer] Export list of tickets to Excel (for user history)
    Workbook exportTicketToExcel(List<Ticket> listOfTickets);

    // [Customer] Reschedule ticket
    TicketDTO rescheduleTicket(String id, LocalDate startDate, LocalDate endDate);
}