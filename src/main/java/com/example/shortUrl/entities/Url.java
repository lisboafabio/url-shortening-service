package com.example.shortUrl.entities;

import com.example.shortUrl.dto.UrlDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "times_accessed")
    private Integer timesAccessed;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Url(UrlDto urlDto) {
        this.url = urlDto.getUrl();
    }

    @PrePersist
    public void prePersist() {
        if (this.timesAccessed == null) {
            this.timesAccessed = 0;
        }
    }

}
