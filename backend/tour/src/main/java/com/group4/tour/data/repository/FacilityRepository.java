package com.group4.tour.data.repository;

import com.group4.tour.data.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {
    List<Facility> findByTourId(String tourId);
}