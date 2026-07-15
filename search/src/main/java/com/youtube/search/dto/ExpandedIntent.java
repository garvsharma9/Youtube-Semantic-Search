package com.youtube.search.dto;

import java.util.List;

public record ExpandedIntent(
    String targetContent,
    String coreVibe,
//    String atmosphericVibe,
    List<String> consolidatedQueries
) {}
