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

        assertThat(facilityDTO)
                .isNotNull()
                .extracting(FacilityDTO::getName, FacilityDTO::getTourId)
                .containsExactly("Swimming Pool", null);
    }

    @Test
    void testToFacilityWithIdDTO() {
        Facility facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");

        FacilityWithIdDTO facilityWithIdDTO = facilityMapper.toFacilityWithIdDTO(facility);

        assertThat(facilityWithIdDTO)
                .isNotNull()
                .extracting(FacilityWithIdDTO::getId, FacilityWithIdDTO::getName, FacilityWithIdDTO::getTourId)
                .containsExactly("facility-id-1", "Swimming Pool", null);
    }

    @Test
    void testToFacility() {
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setName("Swimming Pool");
        facilityDTO.setTourId("tour-id-1");

        Facility facility = facilityMapper.toFacility(facilityDTO);

        assertThat(facility)
                .isNotNull()
                .extracting(Facility::getName, f -> f.getTour().getId())
                .containsExactly("Swimming Pool", "tour-id-1");
    }



    @Test
    void testToFacilityWithIdDTOList() {
        Facility facility = new Facility();
        facility.setId("facility-id-1");
        facility.setName("Swimming Pool");
        List<Facility> facilities = Collections.singletonList(facility);

        List<FacilityWithIdDTO> facilityWithIdDTOList = facilityMapper.toFacilityWithIdDTOList(facilities);

        assertThat(facilityWithIdDTOList)
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo("facility-id-1");
                    assertThat(dto.getName()).isEqualTo("Swimming Pool");
                });
    }
}