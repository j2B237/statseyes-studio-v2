package com.statseyes.studio.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gps_points")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GpsPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "imported_pod_session_id", nullable = false)
    private ImportedPodSessionEntity importedSession;

    @Column(name = "time_ms", nullable = false)        private Integer timeMs;
    @Column(name = "latitude", nullable = false)        private Integer latitude;
    @Column(name = "longitude", nullable = false)       private Integer longitude;
    @Column(name = "speed_cms", nullable = false)       private Integer speedCms;
    @Column(name = "course_deci_deg", nullable = false) private Integer courseDeciDeg;
    @Column(name = "sane", nullable = false)            private Boolean sane;
}
