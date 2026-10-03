package com.statseyes.studio.infrastructure.cache;


public enum CacheType {
    
    ACCOUNT("accounts"),
    ACCOUNT_SUMMARY("account_summary"),
    ACCOUNT_SETTING("account_settings"),
    CLUB("clubs"),
    CLUB_SUMMARY("club_summary"),
    TEAM("teams"),
    ATHLETES("athletes"),
    POSITION("positions"),
    IMPORTED_SESSIONS("imported_sessions"),
    GPS_POINTS("gps-points");

    private final String name;

    CacheType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}