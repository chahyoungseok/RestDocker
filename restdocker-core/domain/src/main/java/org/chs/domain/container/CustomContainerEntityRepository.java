package org.chs.domain.container;


import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.dto.ContainerValidElementsDto;
import org.chs.domain.container.entity.ContainerEntity;
import org.chs.domain.container.enumerate.ContainerStatusEnum;

import java.util.List;

public interface CustomContainerEntityRepository {
    List<ContainerElements> lsContainer(String oauthServiceId);

    ContainerDetailElements inspectContainer(String oauthServiceId, String containerName);

    boolean renameContainer(String containerPk, String postContainerName);

    boolean rmContainer(String containerPk);

    boolean existContainerForImage(String oauthServiceId, String imageName);

    List<ContainerValidElementsDto> findValidElementsListByOAuthServiceId(String oauthServiceId);

    ContainerEntity findContainerByOAuthServiceAndContainerName(String oauthServiceId, String containerName);

    long updateContainerStatus(String containerPk, ContainerStatusEnum containerStatusEnum);
}
