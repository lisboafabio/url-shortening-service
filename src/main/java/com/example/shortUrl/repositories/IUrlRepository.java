package com.example.shortUrl.repositories;

import com.example.shortUrl.entities.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUrlRepository extends JpaRepository<Url, Long> {
    Url findByShortCode(String shortCode);
    Page<Url> findAll(Pageable pageable);
}
