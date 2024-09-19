package com.group4.tour.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class TourRepositoryTest {

    @Autowired
    private TourRepository tourRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        Tour tour1 = new Tour();
        tour1.setTitle("Beach Tour");
        tour1.setDetail("Enjoy the sunny beach");
        tour1.setQuota(20);
        tour1.setPrices(100);
        tour1.setLocation("California");
        tour1.setStatus("Available");
        // Manually setting auditing fields for testing
        tour1.setCreatedAt(now);
        tour1.setUpdatedAt(now);
        tour1.setCreatedBy("testUser");
        tour1.setUpdatedBy("testUser");

        Tour tour2 = new Tour();
        tour2.setTitle("Mountain Hiking");
        tour2.setDetail("Explore the mountains");
        tour2.setQuota(15);
        tour2.setPrices(200);
        tour2.setLocation("Colorado");
        tour2.setStatus("Available");
        // Manually setting auditing fields for testing
        tour2.setCreatedAt(now);
        tour2.setUpdatedAt(now);
        tour2.setCreatedBy("testUser");
        tour2.setUpdatedBy("testUser");

        Tour tour3 = new Tour();
        tour3.setTitle("City Tour");
        tour3.setDetail("Discover the city landmarks");
        tour3.setQuota(30);
        tour3.setPrices(150);
        tour3.setLocation("New York");
        tour3.setStatus("Available");
        // Manually setting auditing fields for testing
        tour3.setCreatedAt(now);
        tour3.setUpdatedAt(now);
        tour3.setCreatedBy("testUser");
        tour3.setUpdatedBy("testUser");

        tourRepository.save(tour1);
        tourRepository.save(tour2);
        tourRepository.save(tour3);
    }

    @Test
    void testFindByTitleContaining() {
        List<Tour> allTours = tourRepository.findAll();
        System.out.println("All tours in the database:");
        allTours.forEach(System.out::println);

        List<Tour> tours = tourRepository.findByTitleContaining("Tour");

        assertThat(tours).hasSize(2);
        assertThat(tours).extracting(Tour::getTitle).containsExactlyInAnyOrder("Beach Tour", "City Tour");

        tours = tourRepository.findByTitleContaining("Beach");
        assertThat(tours).hasSize(1);
        assertThat(tours.get(0).getTitle()).isEqualTo("Beach Tour");
    }


    @Test
    void testFindByPricesBetween() {
        List<Tour> tours = tourRepository.findByPricesBetween(100, 150, Sort.by(Sort.Direction.ASC, "prices"));

        assertThat(tours).hasSize(2);
        assertThat(tours).extracting(Tour::getPrices).containsExactly(100, 150); // Ascending order

        tours = tourRepository.findByPricesBetween(150, 200, Sort.by(Sort.Direction.DESC, "prices"));
        assertThat(tours).hasSize(2);
        assertThat(tours).extracting(Tour::getPrices).containsExactly(200, 150); // Descending order
    }

    @Test
    void testFindByLocationContaining() {
        List<Tour> tours = tourRepository.findByLocationContaining("California");

        assertThat(tours).hasSize(1);
        assertThat(tours.get(0).getLocation()).isEqualTo("California");

        tours = tourRepository.findByLocationContaining("New");
        assertThat(tours).hasSize(1);
        assertThat(tours.get(0).getLocation()).isEqualTo("New York");
    }
}
