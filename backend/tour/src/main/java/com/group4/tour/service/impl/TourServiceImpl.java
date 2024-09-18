package com.group4.tour.service.impl;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import com.group4.tour.exception.ResourceNotFoundException;
import com.group4.tour.service.TourService;
import com.group4.tour.utils.CSVUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TourServiceImpl implements TourService {
    public final TourRepository tourRepository;

    public List<Tour> getAllTours(String title, Integer minPrice, Integer maxPrice, String location, String sortPrice) {
        // Default sorting is by prices ascending
        Sort sort = Sort.by(Sort.Direction.ASC, "prices");

        // If sortPrice is "desc", change the sorting direction
        if (sortPrice != null && sortPrice.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, "prices");
        }

        if (title != null) {
            return tourRepository.findByTitleContaining(title);
        } else if (minPrice != null && maxPrice != null) {
            return tourRepository.findByPricesBetween(minPrice, maxPrice, sort);
        } else if (location != null) {
            return tourRepository.findByLocationContaining(location);
        } else {
            return tourRepository.findAll(sort);
        }
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
        existingTour.setUpdatedTime(updatedTour.getUpdatedTime());
        existingTour.setUpdatedBy(updatedTour.getUpdatedBy());
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
