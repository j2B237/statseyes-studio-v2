package com.statseyes.studio.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imported_pod_sessions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImportedPodSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pod_session_number", nullable = false)
    private Long podSessionNumber;

    @Column(name = "source_file_name", nullable = false)
    private String sourceFileName;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    // SESSION METRICS
    @Column(name = "total_distance_m")   private Double totalDistanceM;
    @Column(name = "max_speed_kmh")      private Double maxSpeedKmh;
    @Column(name = "avg_speed_kmh")      private Double avgSpeedKmh;
    @Column(name = "sprint_count")       private Integer sprintCount;
    @Column(name = "dominant_course_deg") private Double dominantCourseDeg;

    // Athlete/TrainingSession nullable pour l'instant assignation faite
    // plus tard depuis l'UI, pas au moment de l'import brut.
    @Column(name = "athlete_id") private Integer athleteId;

    @Column(name = "account_id")
    private Integer accountId;

    @OneToMany(mappedBy = "importedSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GpsPointEntity> points = new ArrayList<>();
}
