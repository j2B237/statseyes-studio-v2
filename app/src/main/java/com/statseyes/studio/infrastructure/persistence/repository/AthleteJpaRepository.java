package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Transactional
    @Query(
            "UPDATE AthleteEntity a SET a.imageUrl = :imageUrl WHERE " +
            "a.id = :athleteId AND a.account.id = :accountId"
    )
    void updateImageURL(
            @Param("athleteId") Integer athleteId,
            @Param("accountId") Integer accountId,
            @Param("imageUrl") String imageUrl
    );

    Optional<AthleteEntity> findByIdAndAccount_Id(
            Integer athleteId,
            Integer accountId
    );

    List<AthleteEntity> findAllByAccount_Id(Integer id);

    long countByAccount_Id(Integer accountId);
}
