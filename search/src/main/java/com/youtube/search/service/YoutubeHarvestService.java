package com.youtube.search.service;

import com.youtube.search.dto.YouTubeResponse;
import com.youtube.search.dto.YouTubeItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class YoutubeHarvestService {

    private final RestClient restClient;
    private final String apiKey;

    public YoutubeHarvestService(
            RestClient.Builder restClientBuilder,
            @Value("${youtube.api.base-url}") String baseUrl,
            @Value("${youtube.api.key}") String apiKey) {

        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public List<YouTubeItem> fetchVideosForQuery(String query) {
        try {
            YouTubeResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("part", "snippet")
                            .queryParam("maxResults", "4") // Balanced to keep your quota healthy
                            .queryParam("q", query)
                            .queryParam("type", "video")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(YouTubeResponse.class);

            return response != null && response.items() != null ? response.items() : Collections.emptyList();
        } catch (Exception e) {
            // Log the error and return empty list gracefully so your app doesn't crash
            System.err.println("Error calling YouTube API: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}