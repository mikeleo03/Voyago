package com.group4.tour.controller;

import com.group4.tour.data.model.Facility;
import com.group4.tour.dto.FacilityDTO;
import com.group4.tour.mapper.FacilityMapper;
import com.group4.tour.service.FacilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facility")
@AllArgsConstructor
@Validated
public class FacilityController {
    private final FacilityService facilityService;

    private final FacilityMapper facilityMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacilityDTO> createFacility(@RequestBody FacilityDTO facilityDTO) {
        Facility createdFacility = facilityService.createFacility(facilityDTO);
        return ResponseEntity.ok(facilityMapper.toFacilityDTO(createdFacility));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacilityDTO> updateFacility(@PathVariable String id, @RequestBody FacilityDTO facilityDTO) {
        FacilityDTO updatedFacility = facilityService.updateFacility(id, facilityDTO);
        return ResponseEntity.ok(updatedFacility);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFacility(@PathVariable String id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tour/{tourId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<FacilityDTO>> getFacilitiesByTourId(@PathVariable String tourId) {
        List<Facility> facilities = facilityService.getFacilitiesByTourId(tourId);
        return ResponseEntity.ok(facilityMapper.toFacilityDTOList(facilities));
    }
}
