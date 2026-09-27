package com.statseyes.studio.domain.model;

import java.time.LocalDateTime;

public record ImportedSession(
        Integer id,
        long podSessionNumber,
        String sourceFileName,
        LocalDateTime importedAt,
        SessionMetrics metrics
) {
}
