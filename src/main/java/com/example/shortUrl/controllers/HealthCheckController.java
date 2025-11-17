package com.example.shortUrl.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {
    @GetMapping("/health-check")
    public ResponseEntity<Object> getHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", OffsetDateTime.now().toString());

        return ResponseEntity.ok().body(response);
    }
}
