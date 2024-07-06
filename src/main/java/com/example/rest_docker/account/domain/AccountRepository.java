package com.example.rest_docker.account.domain;

import com.example.rest_docker.account.domain.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findByOauthServiceIdEquals(String oauthServiceId);
}
