package com.group4.tour.controller;

import com.group4.tour.data.model.Tour;
import com.group4.tour.service.TourService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tour")
@AllArgsConstructor
@Validated
public class TourController {
    private final TourService tourService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getTours(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sortPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Tour> tours = tourService.getAllTours(title, minPrice, maxPrice, location, sortPrice, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("tours", tours.getContent());
        response.put("currentPage", tours.getNumber());
        response.put("totalItems", tours.getTotalElements());
        response.put("totalPages", tours.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getTourImage(@PathVariable String id) throws MalformedURLException {
        Tour tour = tourService.getTourById(id);
        String imageName = tour.getImage();
        
        // Get the file extension and dynamically determine the content type
        Path path = Paths.get("src/main/resources/static/assets/" + imageName);
        
        if (Files.exists(path)) {
            Resource resource = new UrlResource(path.toUri());
            String contentType;
            try {
                contentType = Files.probeContentType(path);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Set the actual content type dynamically
                    .body(resource);
            } catch (IOException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN', 'CUSTOMER')"))
    public ResponseEntity<Tour> getTourById(@PathVariable String id) {
        Tour tour = tourService.getTourById(id);
        return ResponseEntity.ok(tour);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tour> createTour(
            @RequestPart("tour") Tour tour,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            String imageUrl = tourService.saveImage(file);
            tour.setImage(imageUrl);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.createTour(tour));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tour> updateTour(
            @PathVariable String id,
            @RequestPart("tour") Tour tour,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            String imageUrl = tourService.saveImage(file);
            tour.setImage(imageUrl);
        } else {
            String existingImage = tourService.getTourImageNameById(id);
            tour.setImage(existingImage);
        }

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
