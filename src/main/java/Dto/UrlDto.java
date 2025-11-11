package Dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UrlDto {

    @NotNull(message = "Url cannot be null")
    private String url;

    private final String shortUrl;

    public UrlDto() {
        shortUrl = UUID.randomUUID().toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
