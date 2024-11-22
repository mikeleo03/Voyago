package com.group4.tour.service;

import com.group4.tour.data.model.Facility;
import com.group4.tour.dto.FacilityDTO;

import java.util.List;

public interface FacilityService {
    Facility createFacility(FacilityDTO facilityDTO);
    FacilityDTO updateFacility(String id, FacilityDTO facilityDTO);
    void deleteFacility(String id);
    List<Facility> getFacilitiesByTourId(String tourId);
}