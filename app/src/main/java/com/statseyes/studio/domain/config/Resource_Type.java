package com.statseyes.studio.domain.config;

public enum Resource_Type {
    CLUB("club/"),
    ATHLETE("athlete/");

    private final String value;

    Resource_Type(String value){
        this.value = value;
    }

    public String getValue(){return value;}
}
