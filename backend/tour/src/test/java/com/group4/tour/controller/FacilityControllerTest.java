package com.group4.tour.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.tour.data.model.Facility;
import com.group4.tour.dto.FacilityDTO;
import com.group4.tour.dto.FacilityWithIdDTO;
import com.group4.tour.mapper.FacilityMapper;
import com.group4.tour.service.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class FacilityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FacilityService facilityService;

    @Mock
    private FacilityMapper facilityMapper;

    @InjectMocks
    private FacilityController facilityController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(facilityController).build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateFacility() throws Exception {
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setName("Swimming Pool");
        facilityDTO.setTourId("1");

        Facility facility = new Facility();
        facility.setId("123");
        facility.setName("Swimming Pool");

        when(facilityService.createFacility(any(FacilityDTO.class))).thenReturn(facility);
        when(facilityMapper.toFacilityDTO(any(Facility.class))).thenReturn(facilityDTO);

        mockMvc.perform(post("/api/v1/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facilityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Swimming Pool"))
                .andDo(print());

        verify(facilityService, times(1)).createFacility(any(FacilityDTO.class));
        verify(facilityMapper, times(1)).toFacilityDTO(any(Facility.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateFacility() throws Exception {
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setName("Updated Facility");
        facilityDTO.setTourId("1");

        FacilityDTO updatedFacilityDTO = new FacilityDTO();
        updatedFacilityDTO.setName("Updated Facility");
        updatedFacilityDTO.setTourId("1");

        when(facilityService.updateFacility(anyString(), any(FacilityDTO.class))).thenReturn(updatedFacilityDTO);

        mockMvc.perform(put("/api/v1/facility/{id}", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facilityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Facility"))
                .andDo(print());

        verify(facilityService, times(1)).updateFacility(eq("123"), any(FacilityDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteFacility() throws Exception {
        doNothing().when(facilityService).deleteFacility(anyString());

        mockMvc.perform(delete("/api/v1/facility/{id}", "123"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(facilityService, times(1)).deleteFacility("123");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testGetFacilitiesByTourId() throws Exception {
        Facility facility1 = new Facility();
        facility1.setId("123");
        facility1.setName("Facility 1");

        Facility facility2 = new Facility();
        facility2.setId("124");
        facility2.setName("Facility 2");

        List<Facility> facilities = Arrays.asList(facility1, facility2);

        FacilityWithIdDTO facilityDTO1 = new FacilityWithIdDTO();
        facilityDTO1.setId("123");
        facilityDTO1.setName("Facility 1");

        FacilityWithIdDTO facilityDTO2 = new FacilityWithIdDTO();
        facilityDTO2.setId("124");
        facilityDTO2.setName("Facility 2");

        List<FacilityWithIdDTO> facilityDTOList = Arrays.asList(facilityDTO1, facilityDTO2);

        when(facilityService.getFacilitiesByTourId(anyString())).thenReturn(facilities);
        when(facilityMapper.toFacilityWithIdDTOList(anyList())).thenReturn(facilityDTOList);

        mockMvc.perform(get("/api/v1/facility/tour/{tourId}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Facility 1"))
                .andExpect(jsonPath("$[1].name").value("Facility 2"))
                .andDo(print());

        verify(facilityService, times(1)).getFacilitiesByTourId("1");
        verify(facilityMapper, times(1)).toFacilityWithIdDTOList(anyList());
    }
}