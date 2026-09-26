package com.statseyes.studio.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name="clubs")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubEntity {

    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id 
    private Integer id;

    @Column (name="name", nullable = false)
    private String name;

    @Column (name="short_name", nullable = false)
    private String short_name;

    @Column (name="code", nullable = true, unique = true)
    private String code;

    @Column (name="sport", nullable = false)
    private String sport;

    @Column (name="country", nullable = false)
    private String country;

    @Column (name="city", nullable = false)
    private String city;

    @Column (name="timezone", nullable = false)
    private String timezone;

    @Column (name="language", nullable = false)
    private String language;

    @Column (name="email", nullable = true)
    private String email;

    @Column (name="phone", nullable = true)
    private String phone;

    @Column (name="website", nullable = true)
    private String website;

    @Column (name="logo_url", nullable = true)
    private String logo_url;

    @Column (name="active", nullable = false)
    private boolean active;

    @Column (name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column (name="updated_at", nullable = false)
    private LocalDateTime updated_at;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn (name = "account_id", nullable = false)
    private AccountEntity account;

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        created_at = now;
        updated_at = now;
    }

    @PreUpdate
    private void onUpdate() {
        updated_at = LocalDateTime.now();
    }
    
}