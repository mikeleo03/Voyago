package com.group4.tour.service.impl;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.service.TourService;
import com.group4.tour.utils.CSVUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TourServiceImpl implements TourService {
    public final TourRepository tourRepository;
    private final String uploadDir = "src/main/resources/static/assets/";

    public Page<Tour> getAllTours(String title, Integer minPrice, Integer maxPrice, String location, String sortPrice, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "prices");

        if (sortPrice != null && sortPrice.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, "prices");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        if (title != null) {
            return tourRepository.findByTitleContaining(title, pageable);
        }

        if (minPrice != null && maxPrice != null) {
            return tourRepository.findByPricesBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            return tourRepository.findByPricesGreaterThanEqual(minPrice, pageable);
        } else if (maxPrice != null) {
            return tourRepository.findByPricesLessThanEqual(maxPrice, pageable);
        }

        if (location != null) {
            return tourRepository.findByLocationContaining(location, pageable);
        }

        return tourRepository.findAll(pageable);
    }



    public Tour getTourById(String id) {
        Optional<Tour> tourOptional = tourRepository.findById(id);
        if (tourOptional.isEmpty()){
            throw new ResourceNotFoundException("Tour not found for this id : " + id);
        }
        return tourOptional.get();
    }

    public Tour createTour(Tour tour) {
        return tourRepository.save(tour);
    }


    public String saveImage(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        try {
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save image: " + e.getMessage());
        }
    }

    public String getTourImageNameById(String id) {
        Tour tour = tourRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
        return tour.getImage();
    }

    public Tour updateTour(String id, Tour updatedTour) {
        Optional<Tour> tour = tourRepository.findById(id);
        if (tour.isEmpty()){
            throw new ResourceNotFoundException("Tour not found for this id : " + id);
        }
        Tour existingTour = tour.get();
        existingTour.setTitle(updatedTour.getTitle());
        existingTour.setDetail(updatedTour.getDetail());
        existingTour.setPrices(updatedTour.getPrices());
        existingTour.setQuota(updatedTour.getQuota());
        existingTour.setLocation(updatedTour.getLocation());
        existingTour.setImage(updatedTour.getImage());
        existingTour.setStatus(updatedTour.getStatus());
        existingTour.setUpdatedBy(updatedTour.getUpdatedBy());
        existingTour.setUpdatedAt(updatedTour.getUpdatedAt());
        existingTour.setCreatedBy(updatedTour.getCreatedBy());
        existingTour.setCreatedAt(updatedTour.getCreatedAt());
        return tourRepository.save(existingTour);
    }

    public void reduceQuota(String id, int quantity) {
        Optional<Tour> tour = tourRepository.findById(id);
        if (tour.isEmpty()){
            throw new ResourceNotFoundException("Tour not found for this id : " + id);
        }
        Tour existingTour = tour.get();
        existingTour.setQuota(existingTour.getQuota() - quantity);
        tourRepository.save(existingTour);
    }

    public void updateTourStatus(String id) {
        Optional<Tour> tourOptional = tourRepository.findById(id);
        if (tourOptional.isEmpty()){
            throw new ResourceNotFoundException("Tour not found for this id : " + id);
        }
        Tour tour = tourOptional.get();
        String newStatus = tour.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE";
        tour.setStatus(newStatus);
        tourRepository.save(tour);
    }

    public String importToursFromCsv(MultipartFile file) {
        if (!CSVUtil.isCSVFormat(file)) {
            throw new IllegalArgumentException("File format is not CSV");
        }

        List<Tour> tours = CSVUtil.csvToTours(file);

        tourRepository.saveAll(tours);

        return "CSV import successful. Imported " + tours.size() + " tours.";
    }
}
