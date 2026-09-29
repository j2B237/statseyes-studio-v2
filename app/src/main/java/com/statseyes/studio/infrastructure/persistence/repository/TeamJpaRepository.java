package com.statseyes.studio.infrastructure.persistence.repository;

public interface TeamJpaRepository {
    long countByAccount_Id(Integer teamId);
}


