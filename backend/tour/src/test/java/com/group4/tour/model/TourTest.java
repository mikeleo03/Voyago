package com.group4.tour.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TourTest {
    @Test
    void testTourModel() {
        Tour tour = new Tour();
        tour.setTitle("Beach Adventure");
        tour.setDetail("A fun-filled beach adventure.");
        tour.setPrices(200000);
        tour.setQuota(30);
        tour.setLocation("Bali");
        tour.setStatus("ACTIVE");

        assertEquals("Beach Adventure", tour.getTitle());
        assertEquals("A fun-filled beach adventure.", tour.getDetail());
        assertEquals(200000, tour.getPrices());
        assertEquals(30, tour.getQuota());
        assertEquals("Bali", tour.getLocation());
        assertEquals("ACTIVE", tour.getStatus());
    }
}