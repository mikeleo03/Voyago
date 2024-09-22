package com.group4.tour.mapper;
import com.group4.tour.data.model.Facility;
import com.group4.tour.dto.FacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityMapper INSTANCE = Mappers.getMapper(FacilityMapper.class);

    FacilityDTO toFacilityDTO(Facility facility);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "tour.id", source = "tourId")
    Facility toFacility(FacilityDTO facilityDTO);

    List<FacilityDTO> toFacilityDTOList(List<Facility> facilities);
}
