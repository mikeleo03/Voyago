package com.group4.tour.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tour")
public class TourController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.status(HttpStatus.OK).body("Hello from Tour Service");
    }
}