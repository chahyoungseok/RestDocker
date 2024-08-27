package org.chs.domain.network;

import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;

import java.util.List;

public interface CustomNetworkEntityRepository {
    List<NetworkElements> findByOAuthServiceId(String oauthServiceId);

    NetworkDetailElements inspectNetwork(String oauthServiceId, String networkName);

    boolean rmNetwork(String oauthServiceId, String networkName);
}
