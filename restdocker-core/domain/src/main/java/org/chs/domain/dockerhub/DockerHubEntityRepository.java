package org.chs.domain.dockerhub;

import org.chs.domain.dockerhub.entity.DockerHubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DockerHubEntityRepository extends JpaRepository<DockerHubEntity, String>, CustomDockerHubEntityRepository {
}
