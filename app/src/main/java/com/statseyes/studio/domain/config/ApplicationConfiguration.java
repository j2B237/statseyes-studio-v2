package com.statseyes.studio.domain.config;

public enum ApplicationConfiguration {

    TITLE("Statseyes-Studio");
    private String value;

    ApplicationConfiguration(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
