package com.group4.ticket.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
    private String id;
    private String title;
    private String detail;
    private int quota;
    private int prices;
    private String location;
    private String image;
    private String status;
    private Set<Object> facilities;
    private Set<Object> reviews;
}
