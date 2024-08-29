package org.chs.domain.network;

public interface CustomNetworkContainerMappingEntityRepository {
    long deleteByNetworkPk(String networkPk);

    boolean existNetworkBindingContainer(String oauthServiceId, String networkName);
}
