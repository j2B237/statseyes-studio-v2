package com.statseyes.studio.domain.model;

import java.util.List;

public record PodSessionData(
        long sessionNumber,
        List<GpsData> samples
) {

    // Sous 2 points GPS valides, aucune metrique n'est calculable
    public boolean hasUsableData(){
        return samples.stream().filter(GpsData::Valid).count() >= 2;
    }
}
