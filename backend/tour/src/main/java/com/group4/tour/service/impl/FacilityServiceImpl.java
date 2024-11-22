package com.group4.tour.service.impl;

import com.group4.tour.data.model.Facility;
import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.FacilityRepository;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.dto.FacilityDTO;
import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.mapper.FacilityMapper;
import com.group4.tour.service.FacilityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    private final TourRepository tourRepository;
    private final FacilityMapper facilityMapper;

    @Override
    public Facility createFacility(FacilityDTO facilityDTO) {
        Facility facility = facilityMapper.toFacility(facilityDTO);
        Tour tour = tourRepository.findById(facilityDTO.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + facilityDTO.getTourId()));
        facility.setTour(tour);
        return facilityRepository.save(facility);
    }


    @Override
    public FacilityDTO updateFacility(String id, FacilityDTO facilityDTO) {
        Optional<Facility> existingFacility = facilityRepository.findById(id);
        if (existingFacility.isPresent()) {
            Facility updatedFacility = existingFacility.get();
            updatedFacility.setName(facilityDTO.getName());
            Facility savedFacility = facilityRepository.save(updatedFacility);
            return facilityMapper.toFacilityDTO(savedFacility);
        }
        throw new ResourceNotFoundException("Facility not found with id: " + id);
    }

    @Override
    public void deleteFacility(String id) {
        facilityRepository.deleteById(id);
    }

    @Override
    public List<Facility> getFacilitiesByTourId(String tourId) {
        return facilityRepository.findByTourId(tourId);
    }
}
