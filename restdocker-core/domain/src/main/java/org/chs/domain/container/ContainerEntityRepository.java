package org.chs.domain.container;

import org.chs.domain.container.entity.ContainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerEntityRepository extends JpaRepository<ContainerEntity, String>, CustomContainerEntityRepository {
}
