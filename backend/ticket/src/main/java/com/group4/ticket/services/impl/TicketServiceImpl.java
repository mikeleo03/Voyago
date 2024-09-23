package com.group4.ticket.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group4.ticket.data.model.Ticket;
import com.group4.ticket.data.repository.TicketRepository;
import com.group4.ticket.dto.TicketDTO;
import com.group4.ticket.dto.TicketSaveDTO;
import com.group4.ticket.exceptions.ResourceNotFoundException;
import com.group4.ticket.mapper.TicketMapper;
import com.group4.ticket.services.TicketService;
import com.group4.ticket.utils.ExcelGenerator;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Service
@Validated
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private static final String TICKET_NOT_FOUND = "Ticket not found with id: ";
    private static final String PRICE = "price";

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Ticket> getAllTickets(Integer minPrice, Integer maxPrice, String sortPrice, LocalDate startDate, LocalDate endDate, int page, int size) {
        // Sort logic
        Sort sort = Sort.by(Sort.Direction.ASC, PRICE);
        if ("desc".equalsIgnoreCase(sortPrice)) {
            sort = Sort.by(Sort.Direction.DESC, PRICE);
        }

        // Pageable setup
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build the specification with filters
        Specification<Ticket> spec = TicketSpecification.filterByCriteria(null, minPrice, maxPrice, startDate, endDate);

        // Query the database with the specification and pageable
        return ticketRepository.findAll(spec, pageable);
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<Ticket> getAllTicketsByUserID(String userID, Integer minPrice, Integer maxPrice, LocalDate startDate, LocalDate endDate, int page, int size) {
        // Pageable setup
        Pageable pageable = PageRequest.of(page, size);

        // Build the specification with filters
        Specification<Ticket> spec = TicketSpecification.filterByCriteria(userID, minPrice, maxPrice, startDate, endDate);

        // Query the database with the specification and pageable
        return ticketRepository.findAll(spec, pageable);
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketDTO getTicketById(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            return ticketMapper.toTicketDTO(ticket.get());
        } else {
            throw new ResourceNotFoundException(TICKET_NOT_FOUND + id);
        }
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketDTO createTicket(@Valid TicketSaveDTO ticketSaveDTO) {
        Ticket ticket = ticketMapper.toTicket(ticketSaveDTO);
        // Add any business logic 
        ticket.setId(UUID.randomUUID().toString());
        ticket = ticketRepository.save(ticket);
        return ticketMapper.toTicketDTO(ticket);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TicketDTO updateTicketStatus(String id, String status) {
        if (!"USED".equalsIgnoreCase(status) && !"UNUSED".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Invalid status. Allowed values are 'USED' and 'UNUSED'.");
        }

        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            if (status.equalsIgnoreCase(ticket.getStatus())) {
                throw new IllegalArgumentException("The status is already " + ticket.getStatus() + ".");
            }
            ticket.setStatus(status);
            ticket = ticketRepository.save(ticket);
            return ticketMapper.toTicketDTO(ticket);
        } else {
            throw new ResourceNotFoundException(TICKET_NOT_FOUND + id);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Workbook exportTicketToExcel(List<Ticket> listOfTickets) {
        return ExcelGenerator.generateTicketExcel(listOfTickets);
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketDTO rescheduleTicket(String id, LocalDate startDate, LocalDate endDate) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();

            // Validate if the new schedule conflicts with the current schedule
            if (ticket.getStartDate().isAfter(startDate) && ticket.getEndDate().isBefore(endDate)) {
                throw new IllegalArgumentException("The new start date and end date are overlapping with the current schedule.");
            }

            // Validate if the ticket already rescheduled
            if (ticket.isRescheduled()) {
                throw new IllegalArgumentException("The ticket has already been rescheduled.");
            }

            // Set the new schedule and mark the ticket as rescheduled
            ticket.setStartDate(startDate);
            ticket.setEndDate(endDate);
            ticket.setRescheduled(true);
            ticket = ticketRepository.save(ticket);
            return ticketMapper.toTicketDTO(ticket);
        } else {
            throw new ResourceNotFoundException(TICKET_NOT_FOUND + id);
        }
    }
}

