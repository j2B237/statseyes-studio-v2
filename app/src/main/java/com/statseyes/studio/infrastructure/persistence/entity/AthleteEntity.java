package com.statseyes.studio.infrastructure.persistence.entity;

import com.statseyes.studio.domain.model.AvailabilityStatus;
import com.statseyes.studio.infrastructure.persistence.id.UuidV7;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(name = "athletes")
@NoArgsConstructor
@AllArgsConstructor
public class AthleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name= "uuid", nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 12)
    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @Column(name = "availability_reason")
    private String availabilityReason;

    @Column(name = "availability_since")
    private LocalDate availabilitySince;

    @Column(name = "availability_expected_return")
    private LocalDate availabilityExpectedReturn;

    @Column(name = "firstname", nullable = false, length = 150)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 150)
    private String lastname;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "gender", nullable = false)
    private Character gender;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "athlete_secondary_position",
                joinColumns = @JoinColumn(name = "athlete_id"),
                inverseJoinColumns = @JoinColumn(name = "position_id"))
    @Builder.Default
    private Set<PositionEntity> secondaryPositions = new LinkedHashSet<>();

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionEntity position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Column(name = "height")
    private Double height;

    @Column(name = "max_speed")
    private Double maxSpeed;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "nationality_code", length = 2)
    private String nationalityCode;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Permet de positionner un athlete sur la liste
    // des atheletes disponibles pour le mercato.
    @Column(name = "available", nullable = false)
    private boolean available = false;

    @PrePersist
    void ensureUuid() {
        if (uuid == null) uuid = UuidV7.generate();
    }
}