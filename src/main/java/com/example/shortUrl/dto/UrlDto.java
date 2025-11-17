package com.example.shortUrl.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @NotNull(message = "Url cannot be null")
    @URL(regexp = "^(https|http).*")
    private String url;

    private String shortCode;
}
