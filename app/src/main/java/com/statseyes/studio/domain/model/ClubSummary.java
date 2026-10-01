package com.statseyes.studio.domain.model;


public record ClubSummary(
        Integer id,
        String name,
        String logFilename,
        Integer accountId,
        boolean active
) {
}
