package org.chs.domain.dockerhub;

import org.chs.domain.dockerhub.entity.DockerHubEntity;

public interface CustomDockerHubEntityRepository {

    DockerHubEntity selectDockerImage(String imageName);
}
