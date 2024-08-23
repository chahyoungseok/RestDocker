package org.chs.domain.network;

import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkContainerMappingEntityRepository extends JpaRepository<NetworkContainerMappingEntity, String>, CustomNetworkContainerMappingEntityRepository {
}
