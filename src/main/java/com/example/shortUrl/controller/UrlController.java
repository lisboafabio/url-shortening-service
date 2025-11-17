package com.example.shortUrl.controller;

import com.example.shortUrl.dto.UrlDto;

import com.example.shortUrl.entities.Url;
import com.example.shortUrl.services.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value="/api/url")
public class UrlController {

    @Autowired
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> findAll(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(urlService.findAll(page, size));
    }

    @GetMapping(value = "/{short_code}")
    public ResponseEntity<Object> findByShortUrl(@PathVariable String short_code) {
        Url url = urlService.findByShortCode(short_code);
        if (url != null) {
            return ResponseEntity.ok(url);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UrlDto urlDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.urlService.save(urlDto));
    }

    @PatchMapping(value = "/{short_url}")
    public ResponseEntity<Object> update(@PathVariable String short_url, @RequestBody UrlDto urlDto) {
        if (urlService.update(short_url, urlDto.getUrl())) {
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{short_url}")
    public ResponseEntity<Object> delete(@PathVariable String short_url) {
        if (urlService.delete(short_url)) {
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping(value = "/{short_code}/times-accessed")
    public ResponseEntity<Object> updateTimesAccessed(@PathVariable String short_code) {
        if (urlService.updateTimesAccessed(short_code)) {
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.notFound().build();
    }
}
