package com.group4.tour.service;

import com.group4.tour.data.model.Facility;
import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.FacilityRepository;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.dto.FacilityDTO;
import com.group4.tour.mapper.FacilityMapper;
import com.group4.tour.service.impl.FacilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FacilityServiceImplTest {

    @InjectMocks
    private FacilityServiceImpl facilityService;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private FacilityMapper facilityMapper;

    private FacilityDTO facilityDTO;
    private Facility facility;
    private Tour tour;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        facilityDTO = new FacilityDTO();
        facilityDTO.setName("Swimming Pool");
        facilityDTO.setTourId("tour-id-1");

        tour = new Tour();
        tour.setId("tour-id-1");

        facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");
        facility.setTour(tour);
    }

    @Test
    void testCreateFacility() {
        when(facilityMapper.toFacility(facilityDTO)).thenReturn(facility);
        when(tourRepository.findById(facilityDTO.getTourId())).thenReturn(Optional.of(tour));
        when(facilityRepository.save(facility)).thenReturn(facility);

        Facility createdFacility = facilityService.createFacility(facilityDTO);

        assertThat(createdFacility).isNotNull();
        assertThat(createdFacility.getName()).isEqualTo("Swimming Pool");
        assertThat(createdFacility.getTour()).isEqualTo(tour);

        verify(facilityMapper, times(1)).toFacility(facilityDTO);
        verify(tourRepository, times(1)).findById(facilityDTO.getTourId());
        verify(facilityRepository, times(1)).save(facility);
    }

    @Test
    void testUpdateFacility() {
        String facilityId = "facility-id-1";
        Facility existingFacility = new Facility();
        existingFacility.setId(facilityId);
        existingFacility.setName("Old Name");

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(existingFacility));
        when(facilityMapper.toFacilityDTO(existingFacility)).thenReturn(facilityDTO);
        when(facilityRepository.save(existingFacility)).thenReturn(existingFacility);

        FacilityDTO updatedFacilityDTO = facilityService.updateFacility(facilityId, facilityDTO);

        assertThat(updatedFacilityDTO).isNotNull();
        assertThat(updatedFacilityDTO.getName()).isEqualTo("Swimming Pool");

        verify(facilityRepository, times(1)).findById(facilityId);
        verify(facilityRepository, times(1)).save(existingFacility);
    }

    @Test
    void testUpdateFacilityNotFound() {
        String facilityId = "facility-id-2";

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            facilityService.updateFacility(facilityId, facilityDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Facility not found with id: " + facilityId);

        verify(facilityRepository, times(1)).findById(facilityId);
    }

    @Test
    void testDeleteFacility() {
        String facilityId = "facility-id-1";

        facilityService.deleteFacility(facilityId);

        verify(facilityRepository, times(1)).deleteById(facilityId);
    }

    @Test
    void testGetFacilitiesByTourId() {
        String tourId = "tour-id-1";
        when(facilityRepository.findByTourId(tourId)).thenReturn(List.of(facility));

        List<Facility> facilities = facilityService.getFacilitiesByTourId(tourId);

        assertThat(facilities).isNotEmpty();
        assertThat(facilities.size()).isEqualTo(1);
        assertThat(facilities.get(0).getName()).isEqualTo("Swimming Pool");

        verify(facilityRepository, times(1)).findByTourId(tourId);
    }
}