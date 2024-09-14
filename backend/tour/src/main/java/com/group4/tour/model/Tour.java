package com.group4.tour.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String title;

    private String detail;

    private int quota;

    private int prices;

    private String location;

    private String image;

    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    private Set<Facility> facilities;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private Set<Review> reviews;
}
