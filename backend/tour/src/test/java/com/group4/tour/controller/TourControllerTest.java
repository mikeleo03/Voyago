package com.group4.tour.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.tour.data.model.Tour;
import com.group4.tour.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourService tourService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tour tour;

    @BeforeEach
    void setUp() {
        tour = new Tour();
        tour.setId("1");
        tour.setTitle("Sample Tour");
        tour.setPrices(1000);
        tour.setLocation("Bali");
    }

    @Test
    void testGetTours() throws Exception {
        List<Tour> tours = Arrays.asList(tour);
        Mockito.when(tourService.getAllTours(any(), any(), any(), any(), any())).thenReturn(tours);

        mockMvc.perform(get("/api/v1/tour")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(tour.getTitle()))
                .andExpect(jsonPath("$[0].location").value(tour.getLocation()));
    }

    @Test
    void testGetTourById() throws Exception {
        Mockito.when(tourService.getTourById(anyString())).thenReturn(tour);

        mockMvc.perform(get("/api/v1/tour/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(tour.getTitle()))
                .andExpect(jsonPath("$.location").value(tour.getLocation()));
    }

    @Test
    void testCreateTour() throws Exception {
        Mockito.when(tourService.createTour(any(Tour.class))).thenReturn(tour);

        mockMvc.perform(post("/api/v1/tour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tour)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(tour.getTitle()))
                .andExpect(jsonPath("$.location").value(tour.getLocation()));
    }

    @Test
    void testUpdateTour() throws Exception {
        Mockito.when(tourService.updateTour(anyString(), any(Tour.class))).thenReturn(tour);

        mockMvc.perform(put("/api/v1/tour/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(tour.getTitle()))
                .andExpect(jsonPath("$.location").value(tour.getLocation()));
    }

    @Test
    void testImportToursFromCsv() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "tours.csv", "text/csv", "content".getBytes());
        Mockito.when(tourService.importToursFromCsv(file)).thenReturn("CSV import successful");

        mockMvc.perform(multipart("/api/v1/tour/import").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("CSV import successful"));
    }

    @Test
    void testReduceQuota() throws Exception {
        mockMvc.perform(put("/api/v1/tour/reduce")
                        .param("id", "1")
                        .param("quantity", "2"))
                .andExpect(status().isOk());

        Mockito.verify(tourService, Mockito.times(1)).reduceQuota(anyString(), any(Integer.class));
    }

    @Test
    void testUpdateTourStatus() throws Exception {
        mockMvc.perform(put("/api/v1/tour/status")
                        .param("id", "1"))
                .andExpect(status().isOk());

        Mockito.verify(tourService, Mockito.times(1)).updateTourStatus(anyString());
    }
}
