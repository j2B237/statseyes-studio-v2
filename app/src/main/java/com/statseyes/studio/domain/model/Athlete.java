package com.statseyes.studio.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record Athlete(
        Integer id, String firstname, String lastname, LocalDate birthday, char gender, String imageUrl,
        Integer accountId, Instant createdAt, Instant updatedAt, Integer positionId, String positionName,
        Integer teamId, String teamName, Double height, Double weight, Double maxSpeed, String nationalityCode,
        Integer jerseyNumber, boolean active, boolean available, DominantFoot dominantFoot,
        Availability availability, List<PositionRef> secondaryPositions
) {
    public Athlete(
        Integer id, String firstname, String lastname, LocalDate birthday, char gender, String imageUrl,
        Integer accountId, Instant createdAt, Instant updatedAt, Integer positionId, String positionName,
        Integer teamId, String teamName, Double height, Double weight, Double maxSpeed, String nationalityCode,
        Integer jerseyNumber, boolean active, boolean available
    ){
        this(id,firstname,lastname,birthday,gender,imageUrl,
            accountId,createdAt,updatedAt,positionId,positionName,
            teamId,teamName,height,weight,maxSpeed,nationalityCode,
            jerseyNumber,active,available,DominantFoot.RIGHT,
            Availability.available(),List.of()
        );
    }

    public boolean isAvailableOn(LocalDate day){
        return availability.effectiveOn(day).isAvailable();
    }
}