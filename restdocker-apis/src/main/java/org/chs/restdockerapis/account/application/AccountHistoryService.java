package org.chs.restdockerapis.account.application;

import lombok.RequiredArgsConstructor;
import org.chs.domain.account.AccountLoginHistoryRepository;
import org.chs.domain.account.AccountLogoutHistoryRepository;
import org.chs.domain.account.entity.AccountLoginHistoryEntity;
import org.chs.domain.account.entity.AccountLogoutHistoryEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountHistoryService {

    private final AccountLoginHistoryRepository accountLoginHistoryRepository;
    private final AccountLogoutHistoryRepository accountLogoutHistoryRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveLoginHistory(String createdBy, String ipAddress, boolean failure, String failureReason) {
        accountLoginHistoryRepository.save(
                AccountLoginHistoryEntity.builder()
                        .createdBy(failure ? "NON_JOIN_USER" : createdBy)
                        .ipAddress(ipAddress)
                        .failure(failure)
                        .failureReason(failureReason)
                        .build()
        );
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveLogoutHistory(String ipAddress, boolean failure, String failureReason) {
        accountLogoutHistoryRepository.save(
                AccountLogoutHistoryEntity.builder()
                        .ipAddress(ipAddress)
                        .failure(failure)
                        .failureReason(failureReason)
                        .build()
        );
    }
}
