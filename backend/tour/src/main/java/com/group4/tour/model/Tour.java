package com.group4.tour.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Tour")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Title is mandatory")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Detail is mandatory")
    @Column(name = "detail", nullable = false)
    private String detail;

    @Min(value = 0, message = "Quota must be a positive number")
    @Column(name = "quota", nullable = false)
    private int quota;

    @Min(value = 0, message = "Prices must be a positive number")
    @Column(name = "prices", nullable = false)
    private int prices;

    @NotBlank(message = "Location is mandatory")
    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "image")
    private String image;

    @NotBlank(message = "Status is mandatory")
    @Column(name = "status", nullable = false)
    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
    private Set<Facility> facilities;

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
    private Set<Review> reviews;
}
