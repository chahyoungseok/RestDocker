package org.chs.domain.container.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainEntity;
import org.chs.domain.image.entity.ImageEntity;

@Entity
@Getter
@Table(name = "container")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContainerEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_fk", nullable = false)
    private ImageEntity image;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "pid", nullable = false)
    private String pid;

    @Column(name = "restart_count", nullable = false)
    private String restartCount;
}
