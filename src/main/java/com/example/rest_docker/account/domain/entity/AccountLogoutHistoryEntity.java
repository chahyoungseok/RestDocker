package com.example.rest_docker.account.domain.entity;

import com.example.rest_docker.common.domain.BaseDomainHistoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "account_logout_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountLogoutHistoryEntity extends BaseDomainHistoryEntity {

    @Column(name = "failure", nullable = false)
    private Boolean failure;

    @Column(name = "failure_reason")
    private String failureReason;

    @Builder
    public AccountLogoutHistoryEntity(String ipAddress, Boolean failure, String failureReason) {
        super(ipAddress);
        this.failure = failure;
        this.failureReason = failureReason;
    }
}
