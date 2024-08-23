package org.chs.domain.network.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "network")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetworkEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private AccountEntity account;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "subnet", nullable = false)
    private String subnet;

    @Column(name = "ip_range", nullable = false)
    private String ipRange;

    @Column(name = "gateway", nullable = false)
    private String gateway;
}
