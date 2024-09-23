package com.group4.ticket.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.group4.ticket.data.model.Ticket;
import com.group4.ticket.dto.TicketDTO;
import com.group4.ticket.dto.TicketSaveDTO;
import com.group4.ticket.services.TicketService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket")
@Validated
@AllArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private static final String TICKETS = "tickets";

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllTickets(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String sortPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Ticket> tickets = ticketService.getAllTickets(minPrice, maxPrice, sortPrice, startDate, endDate, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put(TICKETS, tickets.getContent());
        response.put("currentPage", tickets.getNumber());
        response.put("totalItems", tickets.getTotalElements());
        response.put("totalPages", tickets.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getAllTicketsByUserID(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<Ticket> tickets = ticketService.getAllTicketsByUsername((String) authentication.getPrincipal(), minPrice, maxPrice, startDate, endDate, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put(TICKETS, tickets.getContent());
        response.put("currentPage", tickets.getNumber());
        response.put("totalItems", tickets.getTotalElements());
        response.put("totalPages", tickets.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable String id) {
        TicketDTO ticketDTO = ticketService.getTicketById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketSaveDTO ticketSaveDTO) {
        TicketDTO createdTicket = ticketService.createTicket(ticketSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDTO> updateTicketStatus(@PathVariable String id, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");
        TicketDTO updatedTicket = ticketService.updateTicketStatus(id, newStatus);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTicket);
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketDTO> rescheduleTicket(
            @PathVariable String id,
            @RequestBody Map<String, LocalDate> requestBody) {
        LocalDate newStartDate = requestBody.get("startDate");
        LocalDate newEndDate = requestBody.get("endDate");

        TicketDTO rescheduledTicket = ticketService.rescheduleTicket(id, newStartDate, newEndDate);
        return ResponseEntity.status(HttpStatus.OK).body(rescheduledTicket);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public void exportTicketToExcel(@RequestBody Map<String, List<Ticket>> requestBody, HttpServletResponse response) throws IOException {
        List<Ticket> listOfTickets = requestBody.get(TICKETS);
        
        String fileName = "tickets_report.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (Workbook workbook = ticketService.exportTicketToExcel(listOfTickets)) {
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}