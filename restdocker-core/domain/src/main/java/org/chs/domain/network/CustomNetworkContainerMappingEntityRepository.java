package org.chs.domain.network;

import java.util.List;

public interface CustomNetworkContainerMappingEntityRepository {
    long deleteByNetworkPk(String networkPk);

    boolean existNetworkBindingContainer(String oauthServiceId, String networkName);

    List<String> findPrivateIpByOAuthServiceIdAndNetworkName(String oauthServiceId, String networkName);

    long deleteByContainerPk(String containerPk);
}
