package com.group4.tour.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

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
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "prices"));
        Page<Tour> toursPage = tourRepository.findByTitleContaining("Tour", pageable);

        assertThat(toursPage.getContent()).hasSize(2);
        assertThat(toursPage.getContent())
                .extracting(Tour::getTitle)
                .containsExactlyInAnyOrder("Beach Tour", "City Tour");

        toursPage = tourRepository.findByTitleContaining("Beach", pageable);
        assertThat(toursPage.getContent()).hasSize(1);
        assertThat(toursPage.getContent().get(0).getTitle()).isEqualTo("Beach Tour");
    }

    @Test
    void testFindByPricesBetween() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "prices"));
        Page<Tour> toursPage = tourRepository.findByPricesBetween(100, 150, pageable);

        assertThat(toursPage.getContent()).hasSize(2);
        assertThat(toursPage.getContent())
                .extracting(Tour::getPrices)
                .containsExactly(100, 150); // Ascending order

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "prices"));
        toursPage = tourRepository.findByPricesBetween(150, 200, pageable);
        assertThat(toursPage.getContent()).hasSize(2);
        assertThat(toursPage.getContent())
                .extracting(Tour::getPrices)
                .containsExactly(200, 150); // Descending order
    }

    @Test
    void testFindByPricesGreaterThanEqual() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "prices"));
        Page<Tour> toursPage = tourRepository.findByPricesGreaterThanEqual(150, pageable);

        assertThat(toursPage.getContent()).hasSize(2);
        assertThat(toursPage.getContent())
                .extracting(Tour::getPrices)
                .containsExactly(150, 200); // Ascending order
    }

    @Test
    void testFindByPricesLessThanEqual() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "prices"));
        Page<Tour> toursPage = tourRepository.findByPricesLessThanEqual(150, pageable);

        assertThat(toursPage.getContent()).hasSize(2);
        assertThat(toursPage.getContent())
                .extracting(Tour::getPrices)
                .containsExactly(150, 100); // Descending order
    }

    @Test
    void testFindByLocationContaining() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "location"));
        Page<Tour> toursPage = tourRepository.findByLocationContaining("California", pageable);

        assertThat(toursPage.getContent()).hasSize(1);
        assertThat(toursPage.getContent().get(0).getLocation()).isEqualTo("California");

        toursPage = tourRepository.findByLocationContaining("New", pageable);
        assertThat(toursPage.getContent()).hasSize(1);
        assertThat(toursPage.getContent().get(0).getLocation()).isEqualTo("New York");
    }
}
