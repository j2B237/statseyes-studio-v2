package com.statseyes.studio.domain.service;

import com.statseyes.studio.domain.model.GpsData;
import com.statseyes.studio.domain.model.SessionMetrics;

import java.util.List;
public class SessionMetricsCalculator {

    // seuils de detection de sprint — arbitraires pour l'instant, à valider
    private static final double SPRINT_SPEED_THRESHOLD_KMH = 20.0;
    private static final long SPRINT_MIN_DURATION_MS = 1000;
    private static final double ACCEL_THRESHOLD_KMH_PER_S = 1.0;
    private static final double DIRECTION_CHANGE_THRESHOLD_DEG = 30.0;
    private static final long EVENT_COOLDOWN_MS = 1500; // évite de compter plusieurs fois le meme événement continu

    // =======================
    // PUBLIC API
    // =======================

    public SessionMetrics compute(List<GpsData> samples){
        List<GpsData> valid = samples.stream().filter(GpsData::Valid).toList();

        if(valid.size() < 2){
            return new SessionMetrics(0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,0);
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

        // Vitesse moyenne en Kmh
        double avgSpeedKmh = speedSumKmh / valid.size();

        /*
            Indique la direction de déplacement de l'athlète par rapport au nord, exprimée en degrés.
            Pour un athlète en séance d'entraînement, cette donnée est utilisée pour :
                * Mesurer la trajectoire réelle suivie pendant la course ou le déplacement.
                * Calculer la vitesse et l'orientation précise du mouvement.
                * Analyser les changements de direction et l'efficacité du parcours.
         */
        double dominantCourseDeg = (Math.toDegrees(Math.atan2(sumSin, sumCos)) + 360) % 360;

        int accelerationCount = 0;
        int decelerationCount = 0;
        int directionChangeCount = 0;
        long lastAccelMs = Long.MIN_VALUE;
        long lastDecelMs = Long.MIN_VALUE;
        long lastDirChangeMs = Long.MIN_VALUE;


        for(int i = 1; i < valid.size(); i++){

            GpsData prev = valid.get(i - 1);
            GpsData curr = valid.get(i);

            long deltaMs = deltaMillis(prev.Time_MS(), curr.Time_MS());
            if(deltaMs <= 0)continue;

            double prevSpeedKmh = prev.Speed() / 100.0 * 3.6;
            double currSpeedKmh = curr.Speed() / 100.0 * 3.6;

            // Variation de vitesse en sec
            double speedChangePerSec = (currSpeedKmh - prevSpeedKmh) / (deltaMs / 1000.0);

            // On compte le nombre d'acceleration et de deceleration
            if(speedChangePerSec > ACCEL_THRESHOLD_KMH_PER_S && curr.Time_MS() - lastAccelMs > EVENT_COOLDOWN_MS){
                accelerationCount++;
                lastAccelMs = curr.Time_MS();
            } else if (speedChangePerSec < -ACCEL_THRESHOLD_KMH_PER_S && curr.Time_MS() - lastDecelMs > EVENT_COOLDOWN_MS) {
                decelerationCount++;
                lastAccelMs = curr.Time_MS();
            }

            double courseDelta = angularDifference(prev.Course() / 10.0, curr.Course() / 10.0);
            if (courseDelta > DIRECTION_CHANGE_THRESHOLD_DEG && curr.Time_MS() - lastDirChangeMs > EVENT_COOLDOWN_MS) {
                directionChangeCount++;
                lastDirChangeMs = curr.Time_MS();
            }
        }

        long durationSeconds = Math.max(0,
                (valid.getLast().Time_MS() - valid.getFirst().Time_MS()) / 1000);

        double distancePerMinuteM = durationSeconds > 0 ? totalDistanceM / (durationSeconds / 60.0) : 0;

        return new SessionMetrics(
                totalDistanceM, maxSpeedKmh, avgSpeedKmh, sprintCount, dominantCourseDeg,
                durationSeconds, distancePerMinuteM, accelerationCount, decelerationCount, directionChangeCount
        );
    }

    // ==================
    // PRIVATE API
    // ==================

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

    // Difference angulaire correcte (gere le passage 359deg -> 2deg, qui vaut 3deg, pas 357deg)
    private double angularDifference(double a, double b) {
        double diff = Math.abs(a - b) % 360;
        return diff > 180 ? 360 - diff : diff;
    }
}
