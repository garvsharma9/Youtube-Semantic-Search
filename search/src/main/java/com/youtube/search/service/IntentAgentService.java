package com.youtube.search.service;

import com.youtube.search.dto.ExpandedIntent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class IntentAgentService {
    private final ChatModel chatModel;

    public IntentAgentService(ChatModel chatModel)
    {
        this.chatModel = chatModel;
    }

    public ExpandedIntent broadenSearchIntent(String userQuery)
    {
        var outputConverter = new BeanOutputConverter<>(ExpandedIntent.class);

        String formatInstruction = outputConverter.getFormat();

        String prompt = """
                You are a precise media metadata parser. Analyze this search query: "%s"
                
                        You must reply strictly with a single, valid JSON object. Do not include any markdown formatting, conversational text, or explanations.\s
                        Ensure the JSON object is properly closed with a '}' character.
                
                        Use these definitions for the fields:
                        - targetContent: Identify the name of the creator, YouTuber, or show title directly from the query. Keep it exact.
                        - coreVibe: Identify the primary genre or emotional tone of the content (e.g., Comedy, Drama, Tutorial, Vlog).
                        - consolidatedQueries: Generate exactly TWO distinct, natural search phrases for YouTube.
                          * Query 1: Combine the [Creator/Show Name] with the [Core Format/Topic].
                          * Query 2: Combine the [Creator/Show Name] with the [Thematic Vibe/Genre].
                
                        CRITICAL RULES:
                        1. NEVER use plus signs (+), brackets, or symbols in the queries.
                        2. Do not hallucinate external regional languages or locations unless explicitly asked.
                        3. Every single field must contain a valid string value. Do not leave strings empty.
                
                        %s
                """.formatted(userQuery, formatInstruction);

        String response = chatModel.call(prompt);
        return outputConverter.convert(response);
    }
}
