package com.statseyes.studio.domain.service;

import com.statseyes.studio.domain.model.GpsData;
import com.statseyes.studio.domain.model.SessionMetrics;

import java.util.List;
public class SessionMetricsCalculator {

    // Seuils de detection de sprint — arbitraires pour l'instant, a valider
    private static final double SPRINT_SPEED_THRESHOLD_KMH = 20.0;
    private static final long SPRINT_MIN_DURATION_MS = 1000;

    public SessionMetrics compute(List<GpsData> samples){
        List<GpsData> valid = samples.stream().filter(GpsData::Valid).toList();

        if(valid.size() < 2){
            return new SessionMetrics(0, 0, 0, 0, 0);
        }

        double totalDistanceM = 0;
        double maxSpeedKmh = 0;
        double speedSumKmh = 0;
        int sprintCount = 0;
        long sprintRunningMs = 0;
        boolean inSprint = false;
        double sumSin = 0, sumCos = 0;

        for (int i = 0; i < valid.size(); i++) {

            GpsData point = valid.get(i);
            double speedKmh = point.Speed() / 100.0 * 3.6; // cm/s -> km/h

            maxSpeedKmh = Math.max(maxSpeedKmh, speedKmh);
            speedSumKmh += speedKmh;

            double courseRad = Math.toRadians(point.Course() / 10.0);
            sumSin += Math.sin(courseRad);
            sumCos += Math.cos(courseRad);

            if (i > 0) {

                GpsData previous = valid.get(i - 1);
                totalDistanceM += haversineMeters(
                        previous.Latitude(), previous.Longitude(),
                        point.Latitude(), point.Longitude()
                );

                long deltaMs = deltaMillis(previous.Time_MS(), point.Time_MS());

                if (speedKmh >= SPRINT_SPEED_THRESHOLD_KMH) {
                    sprintRunningMs += deltaMs;
                    if (!inSprint && sprintRunningMs >= SPRINT_MIN_DURATION_MS) {
                        sprintCount++;
                        inSprint = true;
                    }
                } else {
                    sprintRunningMs = 0;
                    inSprint = false;
                }
            }
        }

        double avgSpeedKmh = speedSumKmh / valid.size();
        double dominantCourseDeg = (Math.toDegrees(Math.atan2(sumSin, sumCos)) + 360) % 360;

        return new SessionMetrics(
                totalDistanceM, maxSpeedKmh, avgSpeedKmh, sprintCount, dominantCourseDeg);
    }

    private long deltaMillis(int previousTimeMs, int currentTimeMs) {
        long delta = currentTimeMs - previousTimeMs;
        // Passage a minuit (Time_MS repart a 0) : on ignore ce point plutot
        // que de produire un delta negatif absurde. A affiner si des seances
        // traversent minuit en pratique.
        return delta >= 0 ? delta : 0;
    }

    private double haversineMeters(int lat1e7, int lon1e7, int lat2e7, int lon2e7) {
        double lat1 = lat1e7 / 1e7, lon1 = lon1e7 / 1e7;
        double lat2 = lat2e7 / 1e7, lon2 = lon2e7 / 1e7;
        double earthRadiusM = 6_371_000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadiusM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
