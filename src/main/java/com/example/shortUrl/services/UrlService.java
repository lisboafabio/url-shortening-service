package com.example.shortUrl.services;

import com.example.shortUrl.dto.UrlDto;
import com.example.shortUrl.entities.Url;
import com.example.shortUrl.repositories.IUrlRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UrlService {

    private final IUrlRepository urlRepository;

    public UrlService(IUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Cacheable(value = "url", key = "#shortCode")
    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    @Cacheable(value = "allUrls")
    public Page<Url> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return urlRepository.findAll(pageable);
    }

    @CacheEvict(value = "allUrls", allEntries = true)
    public Url save(UrlDto urlDto) {
        Url urlEntity = new Url(urlDto);
        String shortCode = urlRepository.count() + generateShortCode();
        urlEntity.setShortCode(shortCode);

        return urlRepository.save(urlEntity);
    }

    @CacheEvict(value = {"url", "allUrls"}, allEntries = true)
    public boolean update(String shortCode, String url) {
        Url urlEntity = urlRepository.findByShortCode(shortCode);

        if (urlEntity != null) {
            urlEntity.setUrl(url);
            urlEntity.setUpdatedAt(LocalDateTime.now());
            urlRepository.save(urlEntity);
            return true;
        }

        return false;
    }

    @CacheEvict(value = "allUrls", allEntries = true)
    public boolean delete(String shortCode) {
        Url shortCodeFromDatabase = urlRepository.findByShortCode(shortCode);
        if (shortCodeFromDatabase != null) {
            urlRepository.delete(shortCodeFromDatabase);
            return true;
        }

        return false;
    }

    @CacheEvict(value = {"allUrls", "urls"}, allEntries = true)
    public boolean updateTimesAccessed(String short_code) {
        Url url = findByShortCode(short_code);
        System.out.println(url);
        if (url != null) {
            url.setTimesAccessed(url.getTimesAccessed() + 1);
            urlRepository.save(url);
            return true;
        }

        return false;
    }

    private String generateShortCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int length = 7;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

}
