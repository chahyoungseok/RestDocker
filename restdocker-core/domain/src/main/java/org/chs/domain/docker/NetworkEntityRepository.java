package org.chs.domain.docker;

import org.chs.domain.docker.entity.NetworkEntity;
import org.chs.domain.docker.querydsl.CustomNetworkEntityRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkEntityRepository extends JpaRepository<NetworkEntity, String>, CustomNetworkEntityRepository {
}
