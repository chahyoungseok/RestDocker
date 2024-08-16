package org.chs.domain.docker;

import org.chs.domain.docker.entity.NetworkContainerMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkContainerMappingEntityRepository extends JpaRepository<NetworkContainerMappingEntity, String> {
}
