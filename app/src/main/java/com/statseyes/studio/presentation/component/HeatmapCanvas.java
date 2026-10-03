package com.statseyes.studio.presentation.component;

import com.statseyes.studio.domain.model.GpsPoint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class HeatmapCanvas extends Canvas {

    // =====================
    // INSTANCE VARIABLES
    // =====================

    private static final int GRID_COLS = 10;
    private static final int GRID_ROWS = 6;
    private final GraphicsContext gc = getGraphicsContext2D();

    // ====================
    // PUBLIC API
    // ====================

    public HeatmapCanvas() {
        setWidth(600);
        setHeight(360);
    }

    public void render(List<GpsPoint> points){
        gc.clearRect(0, 0, getWidth(), getHeight());

        drawPitch(gc);

        if (points.isEmpty()) return;

        int minLat = points.stream().mapToInt(GpsPoint::latitude).min().orElse(0);
        int maxLat = points.stream().mapToInt(GpsPoint::latitude).max().orElse(1);
        int minLon = points.stream().mapToInt(GpsPoint::longitude).min().orElse(0);
        int maxLon = points.stream().mapToInt(GpsPoint::longitude).max().orElse(1);

        int[][] density = new int[GRID_COLS][GRID_ROWS];
        for (GpsPoint p : points) {
            int col = bucket(p.latitude(), minLat, maxLat, GRID_COLS);
            int row = bucket(p.longitude(), minLon, maxLon, GRID_ROWS);
            density[col][row]++;
        }

        int maxDensity = 1;
        for (int[] col : density) for (int v : col) maxDensity = Math.max(maxDensity, v);

        double cellW = getWidth() / GRID_COLS;
        double cellH = getHeight() / GRID_ROWS;

        for (int c = 0; c < GRID_COLS; c++) {
            for (int r = 0; r < GRID_ROWS; r++) {
                if (density[c][r] == 0) continue;
                double ratio = (double) density[c][r] / maxDensity;
                gc.setFill(heatColor(ratio));
                gc.fillRect(c * cellW, r * cellH, cellW, cellH);
            }
        }
    }

    public void clear(){
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawPitch(gc);

    }

    // =================
    // PRIVATE API
    // =================

    private int bucket(int value, int min, int max, int buckets) {
        if (max == min) return 0;
        int idx = (int) ((double) (value - min) / (max - min) * (buckets - 1));
        return Math.max(0, Math.min(buckets - 1, idx));
    }

    // Vert = peu fréquente, jaune = moyen, rouge = tres fréquente
    private Color heatColor(double ratio) {
        if (ratio < 0.5) return Color.rgb(76, 175, 80, 0.55 + ratio * 0.3);
        return Color.rgb(220, 53, 69, 0.5 + (ratio - 0.5) * 0.7);
    }

    private void drawPitch(GraphicsContext gc) {
        gc.setFill(Color.web("#2E7D32"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(4, 4, getWidth() - 8, getHeight() - 8);
        gc.strokeLine(getWidth() / 2, 4, getWidth() / 2, getHeight() - 4);
        gc.strokeOval(getWidth() / 2 - 40, getHeight() / 2 - 40, 80, 80);
    }
}
