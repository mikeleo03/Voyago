package com.group4.ticket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.group4.ticket.data.model.TicketDetail;
import com.group4.ticket.dto.TicketDetailDTO;
import com.group4.ticket.dto.TicketDetailSaveDTO;

@Mapper(componentModel = "spring")
public interface TicketDetailMapper {
    
    TicketDetailMapper INSTANCE = Mappers.getMapper(TicketDetailMapper.class);

    // TicketDetail - TicketDetailDTO
    TicketDetailDTO toTicketDetailDTO(TicketDetail ticketDetail);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    TicketDetail toTicketDetail(TicketDetailDTO ticketDetailDTO);

    // TicketDetail - TicketDetailSaveDTO
    TicketDetailSaveDTO toTicketDetailSaveDTO(TicketDetail ticketDetail);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    TicketDetail toTicketDetail(TicketDetailSaveDTO ticketDetailSaveDTO);
}
