package com.statseyes.studio.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Availability(
        AvailabilityStatus status,
        String reason,
        LocalDate since,
        LocalDate expectedReturn
) {
    public static Availability available() {
        return new Availability(AvailabilityStatus.AVAILABLE, null, null, null);
    }

    /**
     * Repasse automatiquement à Disponible quand la date de retour est atteinte
     *  calculée à la lecture, sans planificateur
     * */
    public Availability effectiveOn (LocalDate today) {
        if ( status != AvailabilityStatus.AVAILABLE && expectedReturn != null && today.isBefore(expectedReturn)) {
            return available();
        }
        return this;
    }

    public boolean isAvailable() { return status == AvailabilityStatus.AVAILABLE; }

    public Long daysUntilReturn (LocalDate today) {
        return expectedReturn == null ? null : ChronoUnit.DAYS.between(today, expectedReturn);
    }
}
