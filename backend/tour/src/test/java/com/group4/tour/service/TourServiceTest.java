package com.group4.tour.service;

import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourService tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllToursWithPriceSortAsc() {
        Tour tour = new Tour();
        tour.setPrices(1000);
        when(tourRepository.findAll(Sort.by(Sort.Direction.ASC, "prices"))).thenReturn(Collections.singletonList(tour));

        List<Tour> tours = tourService.getAllTours(null, null, null, null, "asc");
        assertFalse(tours.isEmpty());
        assertEquals(1000, tours.get(0).getPrices());
        verify(tourRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "prices"));
    }

    @Test
    void testGetTourByIdNotFound() {
        when(tourRepository.findById(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            tourService.getTourById("nonexistent-id");
        });

        assertEquals("Tour not found for this id : nonexistent-id", exception.getMessage());
    }

    @Test
    void testCreateTour() {
        Tour tour = new Tour();
        when(tourRepository.save(tour)).thenReturn(tour);

        Tour createdTour = tourService.createTour(tour);
        assertNotNull(createdTour);
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testUpdateTour() {
        Tour existingTour = new Tour();
        Tour updatedTour = new Tour();
        when(tourRepository.findById(anyString())).thenReturn(Optional.of(existingTour));
        when(tourRepository.save(existingTour)).thenReturn(existingTour);

        Tour result = tourService.updateTour("some-id", updatedTour);
        assertNotNull(result);
        verify(tourRepository, times(1)).findById("some-id");
        verify(tourRepository, times(1)).save(existingTour);
    }

    @Test
    void testReduceQuota() {
        Tour tour = new Tour();
        tour.setQuota(10);
        when(tourRepository.findById(anyString())).thenReturn(Optional.of(tour));
        when(tourRepository.save(tour)).thenReturn(tour);

        tourService.reduceQuota("some-id", 5);
        assertEquals(5, tour.getQuota());
        verify(tourRepository, times(1)).findById("some-id");
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testUpdateTourStatus() {
        Tour tour = new Tour();
        tour.setStatus("ACTIVE");
        when(tourRepository.findById(anyString())).thenReturn(Optional.of(tour));
        when(tourRepository.save(tour)).thenReturn(tour);

        tourService.updateTourStatus("some-id");
        assertEquals("INACTIVE", tour.getStatus());
        verify(tourRepository, times(1)).findById("some-id");
        verify(tourRepository, times(1)).save(tour);
    }
}