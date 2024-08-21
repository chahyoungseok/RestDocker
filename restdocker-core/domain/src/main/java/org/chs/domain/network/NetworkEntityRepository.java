package org.chs.domain.network;

import org.chs.domain.network.entity.NetworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkEntityRepository extends JpaRepository<NetworkEntity, String>, CustomNetworkEntityRepository {
}
