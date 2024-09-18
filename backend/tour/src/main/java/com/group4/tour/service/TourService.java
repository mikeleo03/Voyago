package com.group4.tour.service;

import com.group4.tour.data.model.Tour;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourService {
    List<Tour> getAllTours(String title, Integer minPrice, Integer maxPrice, String location, String sortPrice);
    Tour getTourById(String id);
    Tour createTour(Tour tour);
    Tour updateTour(String id, Tour updatedTour);
    void reduceQuota(String id, int quantity);
    void updateTourStatus(String id);
    String importToursFromCsv(MultipartFile file);

}
