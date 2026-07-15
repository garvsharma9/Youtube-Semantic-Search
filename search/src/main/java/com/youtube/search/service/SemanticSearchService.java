package com.youtube.search.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanticSearchService {

    private final VectorStore vectorStore;

    public SemanticSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> findSimilarVideos(String userQuery) {
        // Updated to use the Spring AI 2.0.0-M8 Builder syntax
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(4) // Only bring back the absolute top 4 best matches
                         .similarityThreshold(0.75) // Optional threshold
                        .build()
        );
    }
}