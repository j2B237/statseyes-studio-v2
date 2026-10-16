package com.statseyes.studio.domain.model;

public record PodDevice(String id, String label, boolean likelyUsb) {
    @Override public String toString() { return label; }
}