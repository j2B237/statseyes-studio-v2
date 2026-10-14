package com.statseyes.studio.domain.model;

public record AthleteLookupOption (
        Integer id,
        String firstname,
        String lastname,
        Integer positionId,
        String positionName,
        Integer teamId,
        String teamName,
        String imageUrl,
        boolean availableForTransfer
){
}
