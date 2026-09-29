package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AthleteJpaRepository extends JpaRepository<AthleteEntity, Integer>{

    @Query("SELECT a FROM AthleteEntity a " +
            "LEFT JOIN FETCH a.team " +
            "LEFT JOIN FETCH a.position " +
            "WHERE a.account.id = :accountId " +
            "ORDER BY a.firstname ASC")
    List<AthleteEntity> findAllByAccount_IdOrderByFirstnameAscWithTeamAndPosition(
            @Param("accountId") Integer accountId);

    Optional<AthleteEntity> findByAccount_IdAndFirstnameIgnoreCaseAndLastnameIgnoreCase(
            Integer accountId,
            String firstname,
            String lastname
    );

    Optional<AthleteEntity> findByIdAndTeam_IdAndPosition_Id(
            Integer Id,
            Integer teamId,
            Integer positionId
    );

    long countByAccount_Id(Integer accountId);
}
