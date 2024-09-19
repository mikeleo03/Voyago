package com.group4.tour.mapper;

import com.group4.tour.data.model.Tour;
import com.group4.tour.dto.TourDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TourMapper {
    TourMapper INSTANCE = Mappers.getMapper(TourMapper.class);

    TourDTO toTourDTO(Tour tour);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Tour toTour(TourDTO tourDTO);
}
