package com.statseyes.studio.domain.model;

public record LookupOption(
        Integer id,
        String label
) {
    @Override
    public String toString(){
        return label;
    }
}
