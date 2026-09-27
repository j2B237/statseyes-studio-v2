package com.statseyes.studio.domain.model;

import java.util.List;

public record PodSessionData(
        long sessionNumber,
        List<GpsData> samples
) {
}
