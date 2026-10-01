package com.statseyes.studio.domain.config;

public enum PodImportConfig {

    HEADER_SIZE(512),
    BLOCK_SIZE(512),
    RECORD_SIZE(40),
    MAX_LATITUDE_E7(900_000_000),
    MAX_LONGITUDE_E7 (1_800_000_000);
    private final int value;
    PodImportConfig(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
