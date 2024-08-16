package org.chs.domain.docker;

import org.chs.domain.docker.entity.NetworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkEntityRepository extends JpaRepository<NetworkEntity, String> {
}
