package com.example.rest_docker.account.application;

import com.example.rest_docker.account.domain.AccountLoginHistoryRepository;
import com.example.rest_docker.account.domain.AccountLogoutHistoryRepository;
import com.example.rest_docker.account.domain.entity.AccountLoginHistoryEntity;
import com.example.rest_docker.account.domain.entity.AccountLogoutHistoryEntity;
import lombok.RequiredArgsConstructor;
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
