package org.chs.domain.docker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "port_binging")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortBindingEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_fk", nullable = false)
    private ContainerEntity container;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "host_ip", nullable = false)
    private String hostIp;

    @Column(name = "host_port", nullable = false)
    private String hostPort;
}
