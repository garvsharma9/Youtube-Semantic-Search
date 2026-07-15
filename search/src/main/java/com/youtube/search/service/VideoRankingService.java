package com.youtube.search.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoRankingService {

    private final ChatModel chatModel;

    public VideoRankingService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // Single clean record representing each video score item
    public record VideoRank(int index, int vibeMatchScore) {}

    public List<Document> rankVideos(String query, List<Document> documents) {
        if (documents == null || documents.isEmpty()) return documents;

        // 1. Prepare video list text for the model
        StringBuilder videoListBuilder = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            videoListBuilder.append(String.format("Index: %d | %s\n", i, doc.getText()));
        }

        // 2. CRITICAL FIX: Target an Array class directly to seamlessly match Llama's natural output
        var outputConverter = new BeanOutputConverter<>(VideoRank[].class);
        String formatInstruction = outputConverter.getFormat();

        // 3. Keep instructions explicit for the 3B model
        String promptText = """
            You are an advanced AI video ranking engine. Your job is to critically evaluate a list of video items against a user search query.
            
            User Search Query: "%s"
            
            Videos to evaluate:
            %s
            
            INSTRUCTIONS:
            1. Calculate a "vibeMatchScore" from 0 to 100 for each video.
            2. If a video is completely unrelated to the user query, you MUST give it a score of 0.
            3. Sort your output array from the highest score to lowest.
            
            %s
            """.formatted(query, videoListBuilder.toString(), formatInstruction);

        try {
            String response = chatModel.call(promptText);

            // 4. Clean up any markdown wrappers if the model appends them accidentally
            response = response.replaceAll("```json", "").replaceAll("```", "").trim();

            // 5. Convert JSON directly into our Java array
            VideoRank[] rankings = outputConverter.convert(response);

            List<Document> rankedDocs = new ArrayList<>();
            if (rankings != null) {
                for (VideoRank rank : rankings) {
                    int index = rank.index();
                    int score = rank.vibeMatchScore();

                    if (index >= 0 && index < documents.size()) {
                        Document doc = documents.get(index);

                        // Copy existing metadata and embed the validated score
                        Map<String, Object> updatedMetadata = new HashMap<>(doc.getMetadata());
                        updatedMetadata.put("vibeMatchScore", score);

                        rankedDocs.add(new Document(doc.getId(), doc.getText(), updatedMetadata));
                    }
                }
            }
            return rankedDocs;

        } catch (Exception e) {
            System.err.println("AI Reranking failed: " + e.getMessage());
            return documents; // Fallback smoothly to original vector order if an anomaly occurs
        }
    }
}