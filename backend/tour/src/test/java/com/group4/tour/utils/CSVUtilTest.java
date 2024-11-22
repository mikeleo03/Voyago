package com.group4.tour.utils;

import com.group4.tour.data.model.Tour;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVUtilTest {

    @Test
    void testIsCSVFormat() {
        MultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", "title,detail,quota,prices,location".getBytes());
        MultipartFile nonCsvFile = new MockMultipartFile("file", "test.txt", "text/plain", "title,detail,quota,prices,location".getBytes());

        assertTrue(CSVUtil.isCSVFormat(csvFile));
        assertTrue(!CSVUtil.isCSVFormat(nonCsvFile));
    }

    @Test
    void testCsvToTours() throws IOException, CsvValidationException {
        String csvContent = "title,detail,quota,prices,location\n" +
                "Beach Tour,Enjoy the sunny beach,20,100,California\n" +
                "Mountain Hiking,Explore the mountains,15,200,Colorado\n" +
                "City Tour,Discover the city landmarks,30,150,New York";

        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        List<Tour> tours = CSVUtil.csvToTours(file);

        assertEquals(3, tours.size());

        Tour tour1 = tours.get(0);
        assertEquals("Beach Tour", tour1.getTitle());
        assertEquals("Enjoy the sunny beach", tour1.getDetail());
        assertEquals(20, tour1.getQuota());
        assertEquals(100, tour1.getPrices());
        assertEquals("California", tour1.getLocation());
        assertEquals("ACTIVE", tour1.getStatus());

        Tour tour2 = tours.get(1);
        assertEquals("Mountain Hiking", tour2.getTitle());
        assertEquals("Explore the mountains", tour2.getDetail());
        assertEquals(15, tour2.getQuota());
        assertEquals(200, tour2.getPrices());
        assertEquals("Colorado", tour2.getLocation());
        assertEquals("ACTIVE", tour2.getStatus());

        Tour tour3 = tours.get(2);
        assertEquals("City Tour", tour3.getTitle());
        assertEquals("Discover the city landmarks", tour3.getDetail());
        assertEquals(30, tour3.getQuota());
        assertEquals(150, tour3.getPrices());
        assertEquals("New York", tour3.getLocation());
        assertEquals("ACTIVE", tour3.getStatus());
    }
}
