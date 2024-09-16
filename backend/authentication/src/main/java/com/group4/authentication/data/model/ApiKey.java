package com.group4.authentication.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ApiKey")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "theKey", nullable = false, unique = true)
    private String theKey;

    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;
}

