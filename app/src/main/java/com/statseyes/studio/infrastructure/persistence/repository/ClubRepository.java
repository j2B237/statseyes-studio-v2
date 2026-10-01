package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.ClubEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<ClubEntity, Integer> {
    Optional<ClubEntity> findByAccountId(Integer id);
    Optional<ClubEntity> findByIdAndAccount_Id(Integer clubId, Integer accountId);
}