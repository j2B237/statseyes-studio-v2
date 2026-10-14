package com.statseyes.studio.domain.model;

public record LookupFilterOption(
        Integer id,
        String label
) {
    @Override
    public String toString(){
        return label;
    }
}
