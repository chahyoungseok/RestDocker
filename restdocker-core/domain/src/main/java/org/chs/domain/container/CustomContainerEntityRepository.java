package org.chs.domain.container;


import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.dto.ContainerValidElementsDto;

import java.util.List;

public interface CustomContainerEntityRepository {
    List<ContainerElements> lsContainer(String oauthServiceId);

    ContainerDetailElements inspectContainer(String oauthServiceId, String containerName);

    boolean renameContainer(String containerPk, String postContainerName);

    boolean rmContainer(String containerPk);

    boolean existContainerForImage(String oauthServiceId, String imageName);

    String findContainerPk(String oauthServiceId, String containerName);

    List<ContainerValidElementsDto> findValidElementsListByOAuthServiceId(String oauthServiceId);

    String findContainerPkByOAuthServiceAndContainerName(String oauthServiceId, String preContainerName);
}
