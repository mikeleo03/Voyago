package com.group4.tour.controller;

import com.group4.tour.data.model.Tour;
import com.group4.tour.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class TourControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TourService tourService;

    @InjectMocks
    private TourController tourController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tourController).build();
    }

    @Test
    void testGetTours() {
        List<Tour> tourList = Arrays.asList(
                new Tour("1", "Tour 1", "Exciting tour", 100, 500, "Bali", "image1.jpg", "ACTIVE", null, null),
                new Tour("2", "Tour 2", "Amazing adventure", 50, 300, "Jakarta", "image2.jpg", "ACTIVE", null, null)
        );
        Page<Tour> pageTours = new PageImpl<>(tourList, PageRequest.of(0, 10), 2);

        when(tourService.getAllTours(anyString(), anyInt(), anyInt(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(pageTours);

        ResponseEntity<Map<String, Object>> response = tourController.getTours("Tour", 50, 1000, "Bali", "asc", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, ((List<Tour>) response.getBody().get("tours")).size());
        assertEquals(2L, response.getBody().get("totalItems"));
        assertEquals(1, response.getBody().get("totalPages"));

        verify(tourService, times(1)).getAllTours("Tour", 50, 1000, "Bali", "asc", 0, 10);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testGetTourById() throws Exception {
        String tourId = UUID.randomUUID().toString();
        Tour tour = new Tour();
        tour.setId(tourId);
        tour.setTitle("City Tour");

        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tour/{id}", tourId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("City Tour"))
                .andDo(print());

        verify(tourService, times(1)).getTourById(tourId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateTour() throws Exception {
        String tourId = UUID.randomUUID().toString();
        Tour updatedTour = new Tour();
        updatedTour.setId(tourId);
        updatedTour.setTitle("Updated Tour");

        when(tourService.updateTour(eq(tourId), any(Tour.class))).thenReturn(updatedTour);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tour/{id}", tourId)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(updatedTour)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Tour"))
                .andDo(print());

        verify(tourService, times(1)).updateTour(eq(tourId), any(Tour.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testImportToursFromCsv() throws Exception {
        String csvContent = "title,detail,quota,prices,location\n" +
                "Beach Tour,Enjoy the sunny beach,20,100,California\n";

        when(tourService.importToursFromCsv(any())).thenReturn("Import successful");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/tour/import")
                        .file("file", csvContent.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Import successful"))
                .andDo(print());

        verify(tourService, times(1)).importToursFromCsv(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testReduceQuota() throws Exception {
        String tourId = UUID.randomUUID().toString();
        int quantity = 5;

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tour/reduce")
                        .param("id", tourId)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(tourService, times(1)).reduceQuota(tourId, quantity);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateTourStatus() throws Exception {
        String tourId = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tour/status")
                        .param("id", tourId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(tourService, times(1)).updateTourStatus(tourId);
    }
}