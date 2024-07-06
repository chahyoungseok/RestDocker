package com.example.rest_docker.account.domain.entity;

import com.example.rest_docker.common.domain.BaseDomainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountEntity extends BaseDomainEntity {

    @Column(name = "nickname", length = 12, columnDefinition = "CHAR(12)", nullable = false)
    private String nickname;

    @Column(name = "oauth_service_id", nullable = false, unique = true)
    private String oauthServiceId;

    @Column(name = "access_token", nullable = true)
    private String accessToken;

    @Column(name = "refresh_token", nullable = true)
    private String refreshToken;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public AccountEntity(String nickname, String oauthServiceId, String accessToken, String refreshToken, boolean isActive) {
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.oauthServiceId = oauthServiceId;
        this.isActive = isActive;
    }

    public void onActive() {
        this.isActive = true;
    }
}
