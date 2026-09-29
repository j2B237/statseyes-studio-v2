package com.statseyes.studio.infrastructure.persistence.repository;

public interface AthleteJpaRepository {
    long countByAccount_Id(Integer accountId);
}
