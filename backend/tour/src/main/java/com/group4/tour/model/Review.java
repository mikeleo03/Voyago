package com.group4.tour.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourID", nullable = false)
    private Tour tour;

    @NotBlank(message = "User ID is mandatory")
    @Column(name = "userID", nullable = false)
    private String userId;

    @NotBlank(message = "Review is mandatory")
    @Column(name = "review", nullable = false)
    private String review;

    @Range(min = 0, max = 5, message = "Rating must be between 0 and 5")
    @Column(name = "rating", nullable = false)
    private int rating;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String createdBy;

    private String updatedBy;
}
