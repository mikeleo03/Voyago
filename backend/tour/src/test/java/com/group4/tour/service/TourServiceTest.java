package com.group4.tour.service.impl;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.utils.CSVUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourServiceImpl tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetAllToursByTitle() {
        Tour tour = new Tour();
        tour.setTitle("Beach Tour");

        when(tourRepository.findByTitleContaining("Beach")).thenReturn(List.of(tour));

        List<Tour> result = tourService.getAllTours("Beach", null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Beach Tour");
        verify(tourRepository, times(1)).findByTitleContaining("Beach");
    }

    @Test
    void testGetAllToursByPrices() {
        Tour tour = new Tour();
        tour.setPrices(100);

        when(tourRepository.findByPricesBetween(50, 150, Sort.by(Sort.Direction.ASC, "prices"))).thenReturn(List.of(tour));

        List<Tour> result = tourService.getAllTours(null, 50, 150, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrices()).isEqualTo(100);
        verify(tourRepository, times(1)).findByPricesBetween(50, 150, Sort.by(Sort.Direction.ASC, "prices"));
    }

    @Test
    void testGetTourByIdSuccess() {
        Tour tour = new Tour();
        tour.setId("1");
        tour.setTitle("Beach Tour");

        when(tourRepository.findById("1")).thenReturn(Optional.of(tour));

        Tour result = tourService.getTourById("1");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Beach Tour");
        verify(tourRepository, times(1)).findById("1");
    }

    @Test
    void testGetTourByIdNotFound() {
        when(tourRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.getTourById("1"));
        verify(tourRepository, times(1)).findById("1");
    }

    @Test
    void testCreateTour() {
        Tour tour = new Tour();
        tour.setId("1");
        tour.setTitle("Beach Tour");

        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        Tour result = tourService.createTour(tour);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testUpdateTourSuccess() {
        Tour existingTour = new Tour();
        existingTour.setId("1");
        existingTour.setTitle("Old Tour");

        Tour updatedTour = new Tour();
        updatedTour.setTitle("New Tour");

        when(tourRepository.findById("1")).thenReturn(Optional.of(existingTour));
        when(tourRepository.save(any(Tour.class))).thenReturn(existingTour);

        Tour result = tourService.updateTour("1", updatedTour);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Tour");
        verify(tourRepository, times(1)).findById("1");
        verify(tourRepository, times(1)).save(existingTour);
    }

    @Test
    void testUpdateTourNotFound() {
        Tour updatedTour = new Tour();
        updatedTour.setTitle("New Tour");

        when(tourRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.updateTour("1", updatedTour));
        verify(tourRepository, times(1)).findById("1");
    }

    @Test
    void testReduceQuota() {
        Tour existingTour = new Tour();
        existingTour.setId("1");
        existingTour.setQuota(10);

        when(tourRepository.findById("1")).thenReturn(Optional.of(existingTour));

        tourService.reduceQuota("1", 2);

        assertThat(existingTour.getQuota()).isEqualTo(8);
        verify(tourRepository, times(1)).findById("1");
        verify(tourRepository, times(1)).save(existingTour);
    }

    @Test
    void testImportToursFromCsv() {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "id,title,detail\n1,Test Tour,Detail".getBytes());

        when(CSVUtil.isCSVFormat(file)).thenReturn(true);
        when(CSVUtil.csvToTours(file)).thenReturn(List.of(new Tour()));

        String result = tourService.importToursFromCsv(file);

        assertThat(result).contains("CSV import successful");
        verify(tourRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testImportToursFromCsvInvalidFormat() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        when(CSVUtil.isCSVFormat(file)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> tourService.importToursFromCsv(file));
    }
}