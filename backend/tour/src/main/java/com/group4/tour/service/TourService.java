package com.group4.tour.service;

import com.group4.tour.data.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface TourService {
    Page<Tour> getAllTours(String title, Integer minPrice, Integer maxPrice, String location, String sortPrice, int page, int size);
    Tour getTourById(String id);
    Tour createTour(Tour tour);
    String saveImage(MultipartFile file);
    String getTourImageNameById(String id);
    Tour updateTour(String id, Tour updatedTour);
    Tour reduceQuota(String id, int quantity);
    Tour addQuotaByPrice(String id, int price);
    Tour updateTourStatus(String id);
    String importToursFromCsv(MultipartFile file);
}
