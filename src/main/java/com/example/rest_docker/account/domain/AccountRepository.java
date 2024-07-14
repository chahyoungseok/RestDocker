package com.example.rest_docker.account.domain;

import com.example.rest_docker.account.domain.entity.AccountEntity;
import com.example.rest_docker.common.enumerate.ThirdPartyEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findByOauthServiceIdEqualsAndThirdPartyTypeEquals(String oauthServiceId, ThirdPartyEnum thirdPartyType);
}
