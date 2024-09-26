package com.group4.tour.mapper;

import com.group4.tour.data.model.Facility;
import com.group4.tour.dto.FacilityDTO;
import com.group4.tour.dto.FacilityWithIdDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

class FacilityMapperTest {

    private final FacilityMapper facilityMapper = FacilityMapper.INSTANCE;

    @Test
    void testToFacilityDTO() {
        Facility facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");

        FacilityDTO facilityDTO = facilityMapper.toFacilityDTO(facility);

        assertThat(facilityDTO).isNotNull();
        assertThat(facilityDTO.getName()).isEqualTo("Swimming Pool");
        assertThat(facilityDTO.getTourId()).isNull();
    }

    @Test
    void testToFacilityWithIdDTO() {
        Facility facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");

        FacilityWithIdDTO facilityWithIdDTO = facilityMapper.toFacilityWithIdDTO(facility);

        assertThat(facilityWithIdDTO).isNotNull();
        assertThat(facilityWithIdDTO.getId()).isEqualTo("facility-id-1");
        assertThat(facilityWithIdDTO.getName()).isEqualTo("Swimming Pool");
        assertThat(facilityWithIdDTO.getTourId()).isNull();
    }

    @Test
    void testToFacility() {
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setName("Swimming Pool");
        facilityDTO.setTourId("tour-id-1");

        Facility facility = facilityMapper.toFacility(facilityDTO);

        assertThat(facility).isNotNull();
        assertThat(facility.getName()).isEqualTo("Swimming Pool");
        assertThat(facility.getTour()).isNotNull();
        assertThat(facility.getTour().getId()).isEqualTo("tour-id-1");
    }



    @Test
    void testToFacilityWithIdDTOList() {
        Facility facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");
        List<Facility> facilities = Collections.singletonList(facility);

        List<FacilityWithIdDTO> facilityWithIdDTOList = facilityMapper.toFacilityWithIdDTOList(facilities);

        assertThat(facilityWithIdDTOList).isNotNull();
        assertThat(facilityWithIdDTOList).hasSize(1);
        assertThat(facilityWithIdDTOList.get(0).getId()).isEqualTo("facility-id-1");
        assertThat(facilityWithIdDTOList.get(0).getName()).isEqualTo("Swimming Pool");
    }
}