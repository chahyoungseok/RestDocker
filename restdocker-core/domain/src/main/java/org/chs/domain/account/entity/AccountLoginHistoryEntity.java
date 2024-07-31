package org.chs.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainHistoryEntity;

@Entity
@Getter
@Table(name = "account_login_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountLoginHistoryEntity extends BaseDomainHistoryEntity {

    @Column(name = "failure", nullable = false)
    private Boolean failure;

    @Column(name = "failure_reason")
    private String failureReason;

    @Builder
    public AccountLoginHistoryEntity(String ipAddress, String createdBy, Boolean failure, String failureReason) {
        super(createdBy, ipAddress);
        this.failure = failure;
        this.failureReason = failureReason;
    }
}
