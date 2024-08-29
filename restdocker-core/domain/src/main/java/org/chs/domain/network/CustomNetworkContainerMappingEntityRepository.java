package org.chs.domain.network;

public interface CustomNetworkContainerMappingEntityRepository {
    long deleteByNetworkPk(String networkPk);

    boolean existNetworkBindingContainer(String accountPk, String networkName);
}
