package com.group4.tour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDTO {
    private String id;
    private String title;
    private String detail;
    private int quota;
    private int prices;
    private String location;
    private String image;
    private String status;
}
