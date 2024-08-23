package org.chs.domain.image.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "image",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "unique__image___name_tag",
                    columnNames = {
                           "name", "tag"
                    }
            )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private AccountEntity account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "os", nullable = false)
    private String os;

    @Column(name = "architecture", nullable = false)
    private String architecture;

    @Column(name = "size", nullable = false)
    private String size;

    @Builder
    public ImageEntity(AccountEntity account, String name, String tag, String os, String architecture, String size){
        this.account = account;
        this.name = name;
        this.tag = tag;
        this.os = os;
        this.architecture = architecture;
        this.size = size;
    }

    public String getFullName() {
        return this.name + ":" + this.tag;
    }
}
