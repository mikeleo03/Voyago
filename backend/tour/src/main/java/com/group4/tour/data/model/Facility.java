package com.group4.tour.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Facility")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourID", nullable = false)
    private Tour tour;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;
}
