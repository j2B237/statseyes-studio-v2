package com.statseyes.studio.infrastructure.cache;


public enum CacheType {
    
    ACCOUNT("accounts"),
    ACCOUNT_SETTING("account_settings"),
    CLUB("clubs"),
    TEAM("teams"),
    ATHLETE("athletes"),
    POSITION("positions");

    private final String name;

    CacheType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}