package com.statseyes.studio.infrastructure.persistence.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * 
 * AccountEntity
 * <p>
 * This table represents accouterments. The system
 * accept the fact that an account can have at least
 * one email address (Pro and public)
 * </p>
 */

@Entity
@Table(name="account")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String first_name;
    private String last_name;
    private String email;

    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Override
    public String toString() {
        return String.format("Account{\nUsername: " + username + 
                        ",\nFirst name: " + first_name + "\nLast name: " +
                    last_name + "\nEmail: " + email + "\nHashed password: " +
                passwordHash + "\n}");
    }
}