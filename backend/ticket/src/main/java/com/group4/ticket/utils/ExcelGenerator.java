package com.group4.ticket.utils;

import com.group4.ticket.client.TourClient;
import com.group4.ticket.data.model.Ticket;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import reactor.core.publisher.Mono;

public class ExcelGenerator {

    private ExcelGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static Workbook generateTicketExcel(List<Ticket> tickets, TourClient tourClient) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tickets");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Ticket ID", "Customer Name", "Tour Name", "Price", "Start Date", "End Date" };
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Ticket ticket : tickets) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(ticket.getId());
            row.createCell(1).setCellValue(ticket.getUsername());
            
            // Fetch Tour Name from Tour Service
            Mono<String> tourNameMono = tourClient.getTourNameById(ticket.getTourID());
            String tourName = tourNameMono.block();

            row.createCell(2).setCellValue(tourName);
            row.createCell(3).setCellValue(ticket.getPrice());
            row.createCell(4).setCellValue(ticket.getStartDate().toString());
            row.createCell(5).setCellValue(ticket.getEndDate().toString());
        }

        return workbook;
    }
}
