package org.chs.domain.container.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainEntity;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.domain.image.entity.ImageEntity;

@Entity
@Getter
@Table(name = "container",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique__network___account_fk_name",
                        columnNames = {
                                "image_fk", "name"
                        }
                )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContainerEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_fk", nullable = false)
    private ImageEntity image;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private ContainerStatusEnum status;

    @Column(name = "stop_rm", nullable = false)
    private boolean stopRm;

    @Column(name = "private_ip", nullable = false)
    private String privateIp;

    @Column(name = "outer_port", nullable = true)
    private String outerPort;

    @Column(name = "inner_port", nullable = true)
    private String innerPort;

    @Builder
    public ContainerEntity(ImageEntity image, String name, ContainerStatusEnum status, boolean stopRm, String privateIp, String outerPort, String innerPort) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.stopRm = stopRm;
        this.privateIp = privateIp;
        this.outerPort = outerPort;
        this.innerPort = innerPort;
    }
}
