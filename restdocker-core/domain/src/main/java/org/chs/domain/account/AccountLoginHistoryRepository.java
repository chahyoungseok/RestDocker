package org.chs.domain.account;

import org.chs.domain.account.entity.AccountLoginHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountLoginHistoryRepository extends JpaRepository<AccountLoginHistoryEntity, String> {
}
