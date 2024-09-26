package com.group4.tour.service;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.service.impl.TourServiceImpl;
import com.group4.tour.utils.CSVUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private CSVUtil csvUtil;

    @Mock
    private MultipartFile mockMultipartFile;

    @InjectMocks
    private TourServiceImpl tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllToursByTitle() {
        Tour tour = new Tour();
        tour.setTitle("Beach Tour");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "prices"));
        Page<Tour> tourPage = new PageImpl<>(List.of(tour));

        when(tourRepository.findByTitleContaining("Beach", pageable)).thenReturn(tourPage);

        Page<Tour> result = tourService.getAllTours("Beach", null, null, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Beach Tour");
        verify(tourRepository, times(1)).findByTitleContaining("Beach", pageable);
    }

    @Test
    void testGetAllToursByPrices() {
        Tour tour = new Tour();
        tour.setPrices(100);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "prices"));
        Page<Tour> tourPage = new PageImpl<>(List.of(tour));

        when(tourRepository.findByPricesBetween(50, 150, pageable)).thenReturn(tourPage);

        Page<Tour> result = tourService.getAllTours(null, 50, 150, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPrices()).isEqualTo(100);
        verify(tourRepository, times(1)).findByPricesBetween(50, 150, pageable);
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
    void testReduceQuotaNotFound() {
        when(tourRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.reduceQuota("1", 2));
        verify(tourRepository, times(1)).findById("1");
    }

    @Test
    void testUpdateTourStatusToInactive() {
        Tour tour = new Tour();
        tour.setId("1");
        tour.setStatus("ACTIVE");

        when(tourRepository.findById("1")).thenReturn(Optional.of(tour));

        tourService.updateTourStatus("1");

        assertThat(tour.getStatus()).isEqualTo("INACTIVE");
        verify(tourRepository, times(1)).findById("1");
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testUpdateTourStatusToActive() {
        Tour tour = new Tour();
        tour.setId("1");
        tour.setStatus("INACTIVE");

        when(tourRepository.findById("1")).thenReturn(Optional.of(tour));

        tourService.updateTourStatus("1");

        assertThat(tour.getStatus()).isEqualTo("ACTIVE");
        verify(tourRepository, times(1)).findById("1");
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void testUpdateTourStatusTourNotFound() {
        when(tourRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.updateTourStatus("1"));
        verify(tourRepository, times(1)).findById("1");
    }

    @Test
    void testImportToursFromCsvInvalidFormat() {
        when(mockMultipartFile.getContentType()).thenReturn("application/json");

        assertThrows(IllegalArgumentException.class, () -> tourService.importToursFromCsv(mockMultipartFile));

        verify(tourRepository, never()).saveAll(anyList());
    }

    @Test
    void testSaveImageSuccess() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("image content".getBytes());

        String fileName = tourService.saveImage(mockFile);

        assertThat(fileName).contains("test.jpg");
    }

    @Test
    void testGetTourImageNameByIdNotFound() {
        when(tourRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.getTourImageNameById("1"));
    }

    @Test
    void testImportToursFromCsvValidFormat() {
        mockMultipartFile = mock(MultipartFile.class);

        when(mockMultipartFile.getContentType()).thenReturn("text/csv");

        try (MockedStatic<CSVUtil> csvUtilMockedStatic = mockStatic(CSVUtil.class)) {
            csvUtilMockedStatic.when(() -> CSVUtil.isCSVFormat(mockMultipartFile)).thenReturn(true);

            List<Tour> mockTours = List.of(new Tour("1", "Beach Tour", "Beach Vacation", 100, 1000, "Bali", null, "ACTIVE", null, null));
            csvUtilMockedStatic.when(() -> CSVUtil.csvToTours(mockMultipartFile)).thenReturn(mockTours);

            String result = tourService.importToursFromCsv(mockMultipartFile);

            assertThat(result).contains("CSV import successful");
            verify(tourRepository, times(1)).saveAll(mockTours);
        }
    }


}