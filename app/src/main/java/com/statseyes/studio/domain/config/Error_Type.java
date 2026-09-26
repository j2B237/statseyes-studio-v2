package com.statseyes.studio.domain.config;

import lombok.Getter;

@Getter
public enum Error_Type {

    NONE("None"),
    EMPTY_USERNAME("Veuillez renseigner le nom utilisateur"),
    EMPTY_PASSWORD("Veuillez renseigner le mot de passe"),
    AUTHENTICATION_SUCCESS("Authentication success"),
    AUTHENTICATION_FAILED("Aucun compte associe a ce nom d'utilisateur.\nVeuillez creer un compte"),
    SESSION_LOGGING_SUCCESS("Session logging success"),
    SESSION_LOGGING_FAILED("Probleme de connexion a la base de donnees.Essayer a nouveau.");
    private final String message;
    Error_Type(String message){
        this.message = message;
    }

}
