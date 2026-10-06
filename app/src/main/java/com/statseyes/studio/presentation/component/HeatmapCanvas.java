// presentation/component/HeatmapCanvas.java
package com.statseyes.studio.presentation.component;

import com.statseyes.studio.domain.model.GpsPoint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;

public class HeatmapCanvas extends Canvas {

    // Grille fine pour le calcul de densite -- plus elle est fine, plus le
    // rendu final est lisse une fois etire sur le canvas avec interpolation.
    private static final int GRID_COLS = 70;
    private static final int GRID_ROWS = 44;

    // Ecart-type du noyau gaussien, en cellules de grille -- plus grand =
    // taches plus douces/etalees, plus petit = taches plus ponctuelles.
    private static final double SIGMA = 2.4;
    private static final int KERNEL_RADIUS = (int) Math.ceil(SIGMA * 3);

    // Points de degrade : (ratio, couleur avec alpha). Interpoles en continu,
    // pas par paliers -- c'est ce qui donne les transitions douces vert -> rouge.
    private static final double[] STOPS = {0.0, 0.18, 0.4, 0.62, 0.82, 1.0};
    private static final Color[] STOP_COLORS = {
            Color.rgb(76, 175, 80, 0.0),
            Color.rgb(102, 187, 106, 0.42),
            Color.rgb(205, 220, 57, 0.55),
            Color.rgb(255, 235, 59, 0.68),
            Color.rgb(255, 152, 0, 0.78),
            Color.rgb(211, 47, 47, 0.88)
    };

    public HeatmapCanvas() {
        setWidth(640);
        setHeight(400);
        clear();
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawPitchBackground(gc);
        drawPitchMarkings(gc);
    }

    public void render(List<GpsPoint> points) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawPitchBackground(gc);

        if (points != null && !points.isEmpty()) {
            double[][] density = buildDensityGrid(points);
            WritableImage heatImage = paintDensityImage(density);
            gc.setImageSmoothing(true);
            gc.drawImage(heatImage, 0, 0, getWidth(), getHeight());
        }

        drawPitchMarkings(gc);
    }

    // ===================
    // CALCUL DE DENSITE
    // ===================

    private double[][] buildDensityGrid(List<GpsPoint> points) {
        int minLat = points.stream().mapToInt(GpsPoint::latitude).min().orElse(0);
        int maxLat = points.stream().mapToInt(GpsPoint::latitude).max().orElse(1);
        int minLon = points.stream().mapToInt(GpsPoint::longitude).min().orElse(0);
        int maxLon = points.stream().mapToInt(GpsPoint::longitude).max().orElse(1);

        double[][] density = new double[GRID_COLS][GRID_ROWS];

        for (GpsPoint p : points) {
            int col = bucket(p.latitude(), minLat, maxLat, GRID_COLS);
            int row = bucket(p.longitude(), minLon, maxLon, GRID_ROWS);
            splatGaussian(density, col, row);
        }
        return density;
    }

    // Repartit le poids d'un point sur ses cellules voisines selon une
    // gaussienne, plutot que de l'ajouter a une seule case -- c'est ce qui
    // fait fusionner les points proches en taches continues.
    private void splatGaussian(double[][] density, int centerCol, int centerRow) {
        for (int dx = -KERNEL_RADIUS; dx <= KERNEL_RADIUS; dx++) {
            int col = centerCol + dx;
            if (col < 0 || col >= GRID_COLS) continue;
            for (int dy = -KERNEL_RADIUS; dy <= KERNEL_RADIUS; dy++) {
                int row = centerRow + dy;
                if (row < 0 || row >= GRID_ROWS) continue;
                double dist2 = dx * dx + dy * dy;
                density[col][row] += Math.exp(-dist2 / (2 * SIGMA * SIGMA));
            }
        }
    }

    private int bucket(int value, int min, int max, int buckets) {
        if (max == min) return buckets / 2;
        int idx = (int) ((double) (value - min) / (max - min) * (buckets - 1));
        return Math.max(0, Math.min(buckets - 1, idx));
    }

    // ===================
    // RENDU DE L'IMAGE DE CHALEUR
    // ===================

    private WritableImage paintDensityImage(double[][] density) {
        double maxDensity = 0;
        for (double[] col : density) for (double v : col) maxDensity = Math.max(maxDensity, v);
        if (maxDensity == 0) maxDensity = 1;

        WritableImage image = new WritableImage(GRID_COLS, GRID_ROWS);
        PixelWriter writer = image.getPixelWriter();

        for (int c = 0; c < GRID_COLS; c++) {
            for (int r = 0; r < GRID_ROWS; r++) {
                double ratio = density[c][r] / maxDensity;
                writer.setColor(c, r, interpolateColor(ratio));
            }
        }
        return image;
    }

    private Color interpolateColor(double ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        for (int i = 0; i < STOPS.length - 1; i++) {
            if (ratio <= STOPS[i + 1]) {
                double localT = (ratio - STOPS[i]) / (STOPS[i + 1] - STOPS[i]);
                return STOP_COLORS[i].interpolate(STOP_COLORS[i + 1], localT);
            }
        }
        return STOP_COLORS[STOP_COLORS.length - 1];
    }

    // ===================
    // TERRAIN
    // ===================

    private void drawPitchBackground(GraphicsContext gc) {
        double w = getWidth(), h = getHeight();
        int stripeCount = 11;
        double stripeWidth = w / stripeCount;

        for (int i = 0; i < stripeCount; i++) {
            gc.setFill(i % 2 == 0 ? Color.web("#2E7D32") : Color.web("#357A38"));
            gc.fillRect(i * stripeWidth, 0, stripeWidth, h);
        }
    }

    private void drawPitchMarkings(GraphicsContext gc) {
        double w = getWidth(), h = getHeight();
        double margin = w * 0.02;
        double fieldW = w - margin * 2;
        double fieldH = h - margin * 2;

        gc.setStroke(Color.web("#FFFFFF", 0.85));
        gc.setLineWidth(2);

        // Contour du terrain + ligne mediane
        gc.strokeRect(margin, margin, fieldW, fieldH);
        gc.strokeLine(w / 2, margin, w / 2, h - margin);

        // Rond central + point central
        double circleR = fieldH * 0.18;
        gc.strokeOval(w / 2 - circleR, h / 2 - circleR, circleR * 2, circleR * 2);
        fillDot(gc, w / 2, h / 2, 3);

        // Surfaces de reparation + surface de but, des deux cotes
        drawPenaltyArea(gc, margin, margin, fieldW, fieldH, true);
        drawPenaltyArea(gc, margin, margin, fieldW, fieldH, false);

        // Arcs de corner
        double cornerR = fieldH * 0.06;
        gc.strokeArc(margin - cornerR, margin - cornerR, cornerR * 2, cornerR * 2, 270, 90, javafx.scene.shape.ArcType.OPEN);
        gc.strokeArc(margin - cornerR, h - margin - cornerR, cornerR * 2, cornerR * 2, 0, 90, javafx.scene.shape.ArcType.OPEN);
        gc.strokeArc(w - margin - cornerR, margin - cornerR, cornerR * 2, cornerR * 2, 180, 90, javafx.scene.shape.ArcType.OPEN);
        gc.strokeArc(w - margin - cornerR, h - margin - cornerR, cornerR * 2, cornerR * 2, 90, 90, javafx.scene.shape.ArcType.OPEN);
    }

    private void drawPenaltyArea(GraphicsContext gc, double margin, double marginTop, double fieldW, double fieldH, boolean leftSide) {
        double penaltyDepth = fieldW * 0.16;
        double penaltyHeight = fieldH * 0.6;
        double goalDepth = fieldW * 0.055;
        double goalHeight = fieldH * 0.28;

        double x = leftSide ? margin : margin + fieldW - penaltyDepth;
        double y = marginTop + (fieldH - penaltyHeight) / 2;
        gc.strokeRect(x, y, penaltyDepth, penaltyHeight);

        double gx = leftSide ? margin : margin + fieldW - goalDepth;
        double gy = marginTop + (fieldH - goalHeight) / 2;
        gc.strokeRect(gx, gy, goalDepth, goalHeight);

        double spotX = leftSide ? margin + penaltyDepth * 0.72 : margin + fieldW - penaltyDepth * 0.72;
        fillDot(gc, spotX, marginTop + fieldH / 2, 3);

        double arcR = fieldH * 0.16;
        double startAngle = leftSide ? -53 : 127;
        gc.strokeArc(spotX - arcR, marginTop + fieldH / 2 - arcR, arcR * 2, arcR * 2, startAngle, 106, javafx.scene.shape.ArcType.OPEN);
    }

    private void fillDot(GraphicsContext gc, double cx, double cy, double r) {
        gc.setFill(Color.web("#FFFFFF", 0.9));
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);
    }
}