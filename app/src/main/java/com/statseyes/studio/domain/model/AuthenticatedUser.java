package com.statseyes.studio.domain.model;

/**
 * POJO : AuthenticatedUser est un objet immuable, aucune dependance JPA/Hibernate,
 * sur a garder en memoire aussi longtemps sans risque de proxy perime.
 *
 */
public record AuthenticatedUser(
        Integer id,
        String username,
        String firstname,
        String lastname
) {}
