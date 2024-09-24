package com.group4.ticket.mapper;

import com.group4.ticket.data.model.TicketDetailKey;
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
    @Mapping(source = "id.id", target = "id")
    TicketDetailDTO toTicketDetailDTO(TicketDetail ticketDetail);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "id", expression = "java(mapToTicketDetailKey(ticketDetailDTO.getId(), null))")
    TicketDetail toTicketDetail(TicketDetailDTO ticketDetailDTO);

    // TicketDetail - TicketDetailSaveDTO
    TicketDetailSaveDTO toTicketDetailSaveDTO(TicketDetail ticketDetail);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "id", ignore = true)
    TicketDetail toTicketDetail(TicketDetailSaveDTO ticketDetailSaveDTO);

    default String map(TicketDetailKey value) {
        return value != null ? value.getId() : null;
    }

    default TicketDetailKey mapToTicketDetailKey(String id, String ticketId) {
        if (id == null && ticketId == null) {
            return null;
        }
        return new TicketDetailKey(id, ticketId);
    }
}
