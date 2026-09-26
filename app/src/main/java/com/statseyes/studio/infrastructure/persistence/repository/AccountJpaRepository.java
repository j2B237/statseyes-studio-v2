package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Integer>{
    Optional<AccountEntity> findByUsernameIgnoreCase(String username);
}

