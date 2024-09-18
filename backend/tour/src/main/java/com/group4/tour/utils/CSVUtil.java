package com.group4.tour.utils;

import com.group4.tour.model.Tour;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CSVUtil {

    private static final String[] CSV_HEADER = {"title", "detail", "quota", "prices", "location"};
    public static boolean isCSVFormat(MultipartFile file) {
        return Objects.equals(file.getContentType(), "text/csv");
    }

    public static List<Tour> csvToTours(MultipartFile file) {
        List<Tour> tours = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextRecord;
            int lineNumber = 0;

            while ((nextRecord = csvReader.readNext()) != null) {
                if (lineNumber == 0) {
                    lineNumber++;
                    continue;
                }

                Tour tour = new Tour();
                tour.setTitle(nextRecord[0]);
                tour.setDetail(nextRecord[1]);
                tour.setQuota(Integer.parseInt(nextRecord[2]));
                tour.setPrices(Integer.parseInt(nextRecord[3]));
                tour.setLocation(nextRecord[4]);
                tour.setStatus("ACTIVE");

                tours.add(tour);
            }

        } catch (IOException | CsvValidationException | NumberFormatException ex) {
            log.error("Error parsing CSV file: ", ex);
        }

        return tours;
    }
}
