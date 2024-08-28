package org.chs.domain.network.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "network",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique__network___account_fk_name",
                        columnNames = {
                                "account_fk", "name"
                        }
                )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetworkEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private AccountEntity account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subnet", nullable = false)
    private String subnet;

    @Column(name = "ip_range", nullable = true)
    private String ipRange;

    @Column(name = "gateway", nullable = true)
    private String gateway;

    // 컨테이너 간 통신이 가능하게 할지의 여부
    @Column(name = "enable_icc", nullable = false)
    private boolean enableIcc;

    // maximum transmission unit -> 네트워크 인터페이스가 한번에 전송할 수 있는 최대 데이터 패킷 크기
    @Column(name = "mtu", nullable = false)
    private int mtu;

    @Builder
    public NetworkEntity(AccountEntity account, String name, String subnet, String ipRange, String gateway, Boolean enableIcc, Integer mtu) {
        this.account = account;
        this.name = name;
        this.subnet = subnet;
        this.ipRange = ipRange;
        this.gateway = gateway;
        this.enableIcc = enableIcc;
        this.mtu = mtu;
    }
}
