package Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UrlDto {

    @NotNull(message = "Url cannot be null")
    private String url;

    private final String shortUrl;

    public UrlDto() {
        shortUrl = UUID.randomUUID().toString();
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
