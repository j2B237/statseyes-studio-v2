package com.statseyes.studio.domain.model;

import java.text.Normalizer;
import java.util.Locale;

public record AthleteSearchCriteria(
        String text,
        Integer teamId,
        Integer positionId,
        TransferStatusFilter status
) {
    public boolean matches(AthleteLookupOption a){
        return matchesText(a)
                && (teamId == null || teamId.equals(a.teamId()))
                && (positionId == null || positionId.equals(a.positionId()))
                && matchesStatus(a);
    }

    // Chaque mot saisi doit apparaître dans "prénom nom" : "ba ibra" trouve "Ibrahima BA."
    private boolean matchesText(AthleteLookupOption a) {
        String query = normalize(text).trim();
        if (query.isEmpty()) return true;
        String fullName = normalize(a.firstname() + " " + a.lastname());
        for (String token : query.split("\\s+")) {
            if (!fullName.contains(token)) return false;
        }
        return true;
    }

    private boolean matchesStatus(AthleteLookupOption a) {
        TransferStatusFilter s = status == null ? TransferStatusFilter.ALL : status;
        return switch (s) {
            case ALL -> true;
            case AVAILABLE -> a.availableForTransfer();
            case NOT_AVAILABLE -> !a.availableForTransfer();
        };
    }

    // "Aïssatou" == "aissatou"
    private static String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
