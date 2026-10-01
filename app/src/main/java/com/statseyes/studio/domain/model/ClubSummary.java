package com.statseyes.studio.domain.model;


public record ClubSummary(
        Integer id,
        String name,
        String logoFilename,
        Integer accountId,
        boolean active
) {
}
