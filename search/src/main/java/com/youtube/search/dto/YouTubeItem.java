package com.youtube.search.dto;


public record YouTubeItem(Id id, Snippet snippet) {
    public record Id(String videoId) {}
    public record Snippet(String title, String description, String channelTitle) {}
}