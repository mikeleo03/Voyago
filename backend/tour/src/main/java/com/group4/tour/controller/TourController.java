package com.group4.tour.controller;

import com.group4.tour.data.model.Tour;
import com.group4.tour.service.TourService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour")
@AllArgsConstructor
@Validated
public class TourController {
    private final TourService tourService;

    @GetMapping
    @PreAuthorize(("hasAnyRole('ADMIN', 'CUSTOMER')"))
    public ResponseEntity<List<Tour>> getTours(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sortPrice) {
        List<Tour> tours = tourService.getAllTours(title, minPrice, maxPrice, location, sortPrice);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN', 'CUSTOMER')"))
    public ResponseEntity<Tour> getTourById(@PathVariable String id) {
        Tour tour = tourService.getTourById(id);
        return ResponseEntity.ok(tour);
    }

    @PostMapping
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.createTour(tour));
    }

    @PutMapping("/{id}")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<Tour> updateTour(@PathVariable String id, @RequestBody Tour tour) {
        Tour updatedTour = tourService.updateTour(id, tour);
        return updatedTour != null ? ResponseEntity.ok(updatedTour) : ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<String> importToursFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(tourService.importToursFromCsv(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/reduce")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<Void> reduceQuota(@RequestParam String id, @RequestParam int quantity) {
        tourService.reduceQuota(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/status")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<Void> updateTourStatus(@RequestParam String id) {
        tourService.updateTourStatus(id);
        return ResponseEntity.ok().build();
    }
}
