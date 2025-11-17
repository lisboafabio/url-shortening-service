package com.example.shortUrl;

import com.example.shortUrl.dto.UrlDto;
import com.example.shortUrl.entities.Url;
import com.example.shortUrl.repositories.IUrlRepository;
import com.example.shortUrl.services.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=test",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShortUrlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUrlRepository repository;

    @Autowired
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void healthyTest() throws Exception {
        mockMvc.perform(get("/health-check").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getAllShortUrls() throws Exception {
        mockMvc.perform(get("/shorten/all"))
        .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void failedCreateShortUrl() throws Exception {
        mockMvc.perform(
            post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    void failedCreateShortUrlWithWrongUrl() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("url", "google dsad");

        mockMvc.perform(
            post("/shorten")
                    .contentType(MediaType.APPLICATION_JSON).
                    content(new ObjectMapper().writeValueAsString(payload))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createShortUrl() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("url", "https://google.com");

        mockMvc.perform(
            post("/shorten")
            .contentType(MediaType.APPLICATION_JSON).
            content(new ObjectMapper().writeValueAsString(payload))
        ).andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void failedFindShortUrlByCode() throws Exception {
        mockMvc.perform(
                get("/shorten/dsadsa")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getShortUrlById() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        Url urlPersisted = urlService.save(dto);

        mockMvc.perform(
            get("/shorten/"+urlPersisted.getShortCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$.shortCode").value(urlPersisted.getShortCode()));
    }

    @Test
    void failedUpdateShortUrl() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        HashMap<String, String> payload = new HashMap<>();
        payload.put("url", "https://google.com");
        mockMvc.perform(
                put("/shorten/dsadsa")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(payload))
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateShortUrl() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        Url urlPersisted = urlService.save(dto);

        String newUrl = "https://www.google.com";
        HashMap<String, String> payload = new HashMap<>();
        payload.put("url", newUrl);

        mockMvc.perform(
            put("/shorten/".concat(urlPersisted.getShortCode()))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(payload))
        )
        .andExpect(status().isAccepted());
    }

    @Test
    void failedDeleteShortUrl() throws Exception {
        mockMvc.perform(
                delete("/shorten/dsadsa")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteShortUrl() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        Url urlPersisted = urlService.save(dto);

        mockMvc.perform(
            delete("/shorten/"+urlPersisted.getShortCode())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void incrementTimesAccessedShortUrl() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        Url urlPersisted = urlService.save(dto);

        mockMvc.perform(
                put("/shorten/"+urlPersisted.getShortCode()+"/times-accessed")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isAccepted());


        Url urlFromDatabase = repository.findByShortCode(urlPersisted.getShortCode());
        assertThat(urlFromDatabase.getTimesAccessed()).isEqualTo(urlPersisted.getTimesAccessed()+1);
    }

    @Test
    void getUrlStats() throws Exception {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://google.com");
        Url urlPersisted = urlService.save(dto);

        mockMvc.perform(
                get("/shorten/"+urlPersisted.getShortCode()+"/stats")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$").isNotEmpty());
    }
}
