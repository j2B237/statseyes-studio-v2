package com.statseyes.studio.domain.model;

public record SessionMetrics(
        double totalDistanceMeters,
        double maxSpeedKmh,
        double avgSpeedKmh,
        int sprintCount,
        double dominantCourseDegrees,
        long durationSeconds,          // <- cette seule ligne cree automatiquement durationSeconds()
        double distancePerMinuteM,
        int accelerationCount,
        int decelerationCount,
        int directionChangeCount
) {
}
