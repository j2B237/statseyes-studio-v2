package com.statseyes.studio.domain.model;

public enum TransferStatusFilter {

    ALL("Tous les status"),
    AVAILABLE("Disponible pour vente"),
    NOT_AVAILABLE("Non disponible");

    private final String label;

    TransferStatusFilter(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
