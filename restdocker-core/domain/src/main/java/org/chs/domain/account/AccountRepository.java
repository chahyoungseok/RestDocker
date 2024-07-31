package org.chs.domain.account;

import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findByOauthServiceIdEqualsAndThirdPartyTypeEquals(String oauthServiceId, ThirdPartyEnum thirdPartyType);
}
