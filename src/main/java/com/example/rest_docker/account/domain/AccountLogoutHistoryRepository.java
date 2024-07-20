package com.example.rest_docker.account.domain;

import com.example.rest_docker.account.domain.entity.AccountLogoutHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLogoutHistoryRepository extends JpaRepository<AccountLogoutHistoryEntity, String> {
}
