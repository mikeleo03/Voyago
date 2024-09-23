package com.group4.ticket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.group4.ticket.data.model.Ticket;
import com.group4.ticket.dto.TicketDTO;
import com.group4.ticket.dto.TicketSaveDTO;

@Mapper(componentModel = "spring", uses = {TicketDetailMapper.class})
public interface TicketMapper {
    
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    // Ticket - TicketDTO
    TicketDTO toTicketDTO(Ticket ticket);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    Ticket toTicket(TicketDTO ticketDTO);

    // Ticket - TicketSaveDTO
    TicketSaveDTO toTicketSaveDTO(Ticket ticket);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "rescheduled", ignore = true)
    @Mapping(target = "reviewed", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ticketEvidence", ignore = true)
    @Mapping(target = "id", ignore = true)
    Ticket toTicket(TicketSaveDTO ticketSaveDTO);
}
