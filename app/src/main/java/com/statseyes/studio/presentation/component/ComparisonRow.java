package com.statseyes.studio.presentation.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ComparisonRow extends HBox{

    // ========================
    // INSTANCE VARIABLES
    // ========================

    private final Label metricLabel = new Label();
    private final Label valueALabel = new Label();
    private final Label deltaLabel = new Label();
    private final Label valueBLabel = new Label();

    // ======================
    // PUBLIC API
    // ======================

    public ComparisonRow(){
        getStyleClass().add("comparison-row");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        metricLabel.getStyleClass().add("comparison-metric-name");
        valueALabel.getStyleClass().add("comparison-value");
        valueBLabel.getStyleClass().add("comparison-value");
        deltaLabel.getStyleClass().add("comparison-delta");

        HBox.setHgrow(metricLabel, Priority.ALWAYS);
        getChildren().addAll(metricLabel, valueALabel, deltaLabel, valueBLabel);
    }

    public void set(
            String metric, String valueA,
            String valueB, double delta,
            String unit
    ){
        metricLabel.setText(metric);
        valueALabel.setText(valueA);
        valueBLabel.setText(valueB);

        deltaLabel.getStyleClass().removeAll("delta-up", "delta-down", "delta-flat");
        if (Math.abs(delta) < 0.01) {
            deltaLabel.setText("=");
            deltaLabel.getStyleClass().add("delta-flat");
        } else if (delta > 0) {
            deltaLabel.setText("▲ +" + round(delta) + " " + unit);
            deltaLabel.getStyleClass().add("delta-up");
        } else {
            deltaLabel.setText("▼ " + round(delta) + " " + unit);
            deltaLabel.getStyleClass().add("delta-down");
        }
    }

    private String round(double v) {
        return String.format("%.1f", v);
    }
}
