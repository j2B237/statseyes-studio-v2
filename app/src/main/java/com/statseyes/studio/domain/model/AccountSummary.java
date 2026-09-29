package com.statseyes.studio.domain.model;

/**
 * Une seule porte de lecture plutôt que trois
 * Le résumé agrège trois choses (athlètes, équipes, imports).
 * On passe par un port dédié en lecture seule plutôt que de faire transiter le calcul
 * par AthleteRepositoryPort, TeamRepositoryPort et ImportedSessionRepositoryPort.
 * Sinon, il faudrait ajouter des méthodes de comptage à trois ports d'écriture,
 * uniquement pour un écran. Un port de lecture unique permet aussi de faire les quatre
 * requêtes dans une seule transaction, avec des COUNT SQL.
 * On évite ainsi de charger toutes les lignes pour les compter,
 * ce que ferait un findAll().size()
 */

import java.time.LocalDateTime;

public record AccountSummary(
    long athleteCount,
    long teamCount,
    long importedSessionCount,
    LocalDateTime lastImportedAt        // Null si aucun
) {
}
