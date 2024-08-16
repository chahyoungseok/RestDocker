package org.chs.domain.docker;

import org.chs.domain.docker.entity.ContainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerEntityRepository extends JpaRepository<ContainerEntity, String> {
}
