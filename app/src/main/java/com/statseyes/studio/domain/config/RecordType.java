package com.statseyes.studio.domain.config;

public enum RecordType{
    RECORD_TYPE_EMPTY(0),
    RECORD_TYPE_SAMPLE(1),
    RECORD_TYPE_SESSION_START(2),
    RECORD_TYPE_SESSION_END(3),
    ;

    private final int value;
    RecordType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
