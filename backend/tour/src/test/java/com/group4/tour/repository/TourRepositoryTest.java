package com.group4.tour.repository;

import com.group4.tour.data.model.Tour;
import com.group4.tour.data.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TourRepositoryTest {
    @Autowired
    private TourRepository tourRepository;

    @BeforeEach
    void setUp() {
        tourRepository.deleteAll();
    }

    @Test
    void testSaveAndFindTourById() {
        Tour tour = new Tour();
        tour.setTitle("Mountain Trek");
        tour.setDetail("A challenging mountain trek.");
        tour.setPrices(300000);
        tour.setQuota(10);
        tour.setLocation("Himalaya");
        tour.setStatus("ACTIVE");

        Tour savedTour = tourRepository.save(tour);

        Optional<Tour> foundTour = tourRepository.findById(savedTour.getId());

        assertTrue(foundTour.isPresent());
        assertEquals("Mountain Trek", foundTour.get().getTitle());
        assertEquals("A challenging mountain trek.", foundTour.get().getDetail());
    }

    @Test
    void testFindByTitleContaining() {
        Tour tour1 = new Tour();
        tour1.setTitle("Ocean Dive");
        tour1.setDetail("Explore the ocean depths.");
        tour1.setPrices(250000);
        tour1.setQuota(20);
        tour1.setLocation("Great Barrier Reef");
        tour1.setStatus("ACTIVE");

        Tour tour2 = new Tour();
        tour2.setTitle("Mountain Adventure");
        tour2.setDetail("Experience the thrill of the mountains.");
        tour2.setPrices(150000);
        tour2.setQuota(15);
        tour2.setLocation("Rockies");
        tour2.setStatus("ACTIVE");

        tourRepository.save(tour1);
        tourRepository.save(tour2);

        List<Tour> tours = tourRepository.findByTitleContaining("Adventure");

        assertEquals(1, tours.size());
        assertEquals("Mountain Adventure", tours.get(0).getTitle());
    }
}