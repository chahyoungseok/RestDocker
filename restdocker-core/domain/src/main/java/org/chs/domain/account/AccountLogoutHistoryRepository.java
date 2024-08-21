package org.chs.domain.account;

import org.chs.domain.account.entity.AccountLogoutHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLogoutHistoryRepository extends JpaRepository<AccountLogoutHistoryEntity, String>, CustomAccountLogoutHistoryRepository {
}
