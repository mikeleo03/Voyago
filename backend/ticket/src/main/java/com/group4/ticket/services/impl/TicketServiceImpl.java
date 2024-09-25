package com.group4.ticket.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.group4.ticket.client.PaymentClient;
import com.group4.ticket.client.TourClient;
import com.group4.ticket.data.model.Ticket;
import com.group4.ticket.data.model.TicketDetail;
import com.group4.ticket.data.model.TicketDetailKey;
import com.group4.ticket.data.repository.TicketDetailRepository;
import com.group4.ticket.data.repository.TicketRepository;
import com.group4.ticket.dto.TicketDTO;
import com.group4.ticket.dto.TicketDetailDTO;
import com.group4.ticket.dto.TicketDetailSaveDTO;
import com.group4.ticket.dto.TicketSaveDTO;
import com.group4.ticket.exceptions.ResourceNotFoundException;
import com.group4.ticket.mapper.TicketDetailMapper;
import com.group4.ticket.mapper.TicketMapper;
import com.group4.ticket.services.TicketService;
import com.group4.ticket.utils.ExcelGenerator;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import reactor.core.publisher.Mono;

@Service
@Validated
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    
    private final TicketRepository ticketRepository;
    private final TicketDetailRepository ticketDetailRepository;
    private final TicketMapper ticketMapper;
    private final TicketDetailMapper ticketDetailMapper;
    private final TourClient tourClient;
    private final PaymentClient paymentClient;
    private static final String TICKET_NOT_FOUND = "Ticket not found with id: ";
    private static final String PRICE = "price";

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketDetailRepository ticketDetailRepository, TicketMapper ticketMapper, TicketDetailMapper ticketDetailMapper, TourClient tourClient, PaymentClient paymentClient) {
        this.ticketRepository = ticketRepository;
        this.ticketDetailRepository = ticketDetailRepository;
        this.ticketMapper = ticketMapper;
        this.ticketDetailMapper = ticketDetailMapper;
        this.tourClient = tourClient;
        this.paymentClient = paymentClient;
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
    public Page<Ticket> getAllTicketsByUsername(String username, Integer minPrice, Integer maxPrice, LocalDate startDate, LocalDate endDate, int page, int size) {
        // Pageable setup
        Pageable pageable = PageRequest.of(page, size);

        // Build the specification with filters
        Specification<Ticket> spec = TicketSpecification.filterByCriteria(username, minPrice, maxPrice, startDate, endDate);

        // Query the database with the specification and pageable
        return ticketRepository.findAll(spec, pageable);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public TicketDTO getTicketById(String id) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();

            Set<TicketDetail> ticketDetails = ticketDetailRepository.findByTicket(ticket);

            Set<TicketDetailDTO> ticketDetailDTOs = new HashSet<>();
            for (TicketDetail ticketDetail : ticketDetails) {
                TicketDetailDTO ticketDetailDTO = ticketDetailMapper.toTicketDetailDTO(ticketDetail);
                ticketDetailDTOs.add(ticketDetailDTO);
            }

            TicketDTO ticketDTO = ticketMapper.toTicketDTO(ticket);
            ticketDTO.setTicketDetails(ticketDetailDTOs);

            return ticketDTO;
        } else {
            throw new ResourceNotFoundException(TICKET_NOT_FOUND + id);
        }
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketDTO createTicket(@Valid TicketSaveDTO ticketSaveDTO) {
        // Step 1: Map ticketSaveDTO to Ticket entity
        logger.info("Save DTO: {}", ticketSaveDTO);
        Ticket ticket = ticketMapper.toTicket(ticketSaveDTO);
        logger.info("Ticket: {}", ticket);

        // Step 2: Calculate the number of days (ensure dates are valid)
        LocalDate startDate = ticketSaveDTO.getStartDate();
        LocalDate endDate = ticketSaveDTO.getEndDate();
        long numberOfDays = 0;

        if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
            numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        }

        if (numberOfDays <= 0) {
            logger.warn("Invalid dates provided, setting ticket price to 0");
            ticket.setPrice(0);
        } else {
            // Step 3: Set the ticket price based on the tour
            Mono<Integer> tourPriceMono = tourClient.getTourPriceById(ticket.getTourID());
            Integer pricePerDay = tourPriceMono.block();
            Integer quantity = ticketSaveDTO.getTicketDetails().size();
            
            // Multiply by the number of days and the number of tickets (quantity)
            ticket.setPrice(pricePerDay * quantity * (int) numberOfDays);
        }

        // Step 4: Generate payment and get the paymentID
        Mono<String> paymentIDMono = paymentClient.createPayment(ticket.getPrice());
        String paymentID = paymentIDMono.block();
        ticket.setPaymentID(paymentID);

        // Step 5: Save the ticket entity (this will cascade and save the ticket details as well)
        logger.info("Saving ticket with TourID: {}", ticket.getTourID());
        Ticket savedTicket = ticketRepository.save(ticket);  // Ensure cascade is enabled
        logger.info("Ticket created successfully with ID: {}", savedTicket.getId());

        Set<TicketDetail> ticketDetails = new HashSet<>();

        // Step 6: Set the ticket reference for each TicketDetail and save
        for (TicketDetailSaveDTO ticketDetailSaveDTO : ticketSaveDTO.getTicketDetails()) {
            TicketDetailKey key = new TicketDetailKey(UUID.randomUUID().toString(), savedTicket.getId());
            TicketDetail ticketDetail = ticketDetailMapper.toTicketDetail(ticketDetailSaveDTO);
            ticketDetail.setId(key); // Set the key to each detail
            ticketDetail.setTicket(savedTicket); // Assign the saved ticket to each detail

            ticketDetails.add(ticketDetail); // Add the detail to the list
        }

        // Step 7: Save the ticket details (this is usually done automatically if cascading is set correctly)
        ticketDetailRepository.saveAll(ticketDetails);

        // Step 8: Update the tour quantity
        tourClient.updateTourQuantityById(savedTicket.getTourID(), savedTicket.getTicketDetails().size()).block();

        // Return the mapped TicketDTO
        return ticketMapper.toTicketDTO(savedTicket);
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
        return ExcelGenerator.generateTicketExcel(listOfTickets, tourClient);
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

    @Override
    public Ticket returnTourQuota(String paymentID) {
        // Search the ticket based on given payemnt ID
        logger.info("Return tour quota with paymentID: {}", paymentID);
        Optional<Ticket> ticketOptional = ticketRepository.findByPaymentID(paymentID);
        if (ticketOptional.isPresent()) {
            // Get the tour sizes
            Ticket ticket = ticketOptional.get();
            logger.info("Ticket existed: {}", ticket);
            // Update the tour quantity
            tourClient.returnTourQuantityByPrice(ticket.getTourID(), ticket.getPrice()).block();
            logger.info("Ticket update complete");
            return ticket;
        } else {
            logger.info("Ticket not");
            throw new ResourceNotFoundException(TICKET_NOT_FOUND + paymentID);
        }
    }
}

