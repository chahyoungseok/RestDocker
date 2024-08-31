package org.chs.domain.network;

import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.domain.network.entity.NetworkEntity;

import java.util.List;

public interface CustomNetworkEntityRepository {
    List<NetworkElements> findByOAuthServiceId(String oauthServiceId);

    NetworkDetailElements inspectNetwork(String oauthServiceId, String networkName);

    boolean rmNetwork(String oauthServiceId, String networkName);

    NetworkEntity findByOAuthServiceIdAndNetworkName(String oauthServiceId, String networkName);
}
