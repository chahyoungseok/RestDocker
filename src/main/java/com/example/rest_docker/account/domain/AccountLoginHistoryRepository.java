package com.example.rest_docker.account.domain;

import com.example.rest_docker.account.domain.entity.AccountLoginHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLoginHistoryRepository extends JpaRepository<AccountLoginHistoryEntity, String> {
}
