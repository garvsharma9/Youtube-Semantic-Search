package com.youtube.search.service;

import com.youtube.search.dto.YouTubeItem;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorIngestionService {

    private final VectorStore vectorStore;

    public VectorIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestVideos(List<YouTubeItem> videos) {
        if (videos == null || videos.isEmpty()) {
            return;
        }

        // Convert YouTube items into Spring AI Documents
        List<Document> documents = videos.stream().map(video -> {
            // The text the AI will actually "read" and vectorize
            String semanticContent = "Title: " + video.snippet().title() +
                    "\nDescription: " + video.snippet().description();

            // The metadata we need to retrieve the video later
            Map<String, Object> metadata = Map.of(
                    "videoId", video.id().videoId(),
                    "channelTitle", video.snippet().channelTitle()
            );

            return new Document(semanticContent, metadata);
        }).collect(Collectors.toList());

        // Send to MongoDB Atlas (Ollama will automatically embed this text in the background!)
        vectorStore.add(documents);
        System.out.println("Successfully ingested " + documents.size() + " videos into MongoDB!");
    }
}