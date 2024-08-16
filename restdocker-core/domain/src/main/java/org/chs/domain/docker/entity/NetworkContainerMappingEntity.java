package org.chs.domain.docker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "network_container")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetworkContainerMappingEntity extends BaseDomainEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_fk", nullable = false)
    private ContainerEntity container;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "network_fk", nullable = false)
    private NetworkEntity network;
}
