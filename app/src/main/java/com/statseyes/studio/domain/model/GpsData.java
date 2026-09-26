package com.statseyes.studio.domain.model;

public record GpsData(
        Integer Latitude,
        Integer Longitude,
        Integer Speed , // cm/s
        Integer Time_MS, // ms
        Integer Course,
        Boolean Valid
) {
}
