package com.group4.tour.data.repository;

import com.group4.tour.data.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, String> {
    Page<Tour> findByTitleContaining(String title, Pageable pageable);
    Page<Tour> findByPricesBetween(Integer minPrice, Integer maxPrice, Pageable pageable);
    Page<Tour> findByPricesGreaterThanEqual(Integer minPrice, Pageable pageable);
    Page<Tour> findByPricesLessThanEqual(Integer maxPrice, Pageable pageable);
    Page<Tour> findByLocationContaining(String location, Pageable pageable);
    Page<Tour> findAll(Pageable pageable);
}
