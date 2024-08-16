package org.chs.domain.docker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private AccountEntity account;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "os", nullable = false)
    private String os;

    @Column(name = "size", nullable = false)
    private String size;

}
