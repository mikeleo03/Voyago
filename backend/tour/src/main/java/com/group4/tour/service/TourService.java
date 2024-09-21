package com.group4.tour.service;

import com.group4.tour.data.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface TourService {
    Page<Tour> getAllTours(String title, Integer minPrice, Integer maxPrice, String location, String sortPrice, int page, int size);
    Tour getTourById(String id);
    Tour createTour(Tour tour);
    Tour updateTour(String id, Tour updatedTour);
    void reduceQuota(String id, int quantity);
    void updateTourStatus(String id);
    String importToursFromCsv(MultipartFile file);

}
