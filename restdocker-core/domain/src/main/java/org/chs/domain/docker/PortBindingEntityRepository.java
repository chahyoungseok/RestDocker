package org.chs.domain.docker;

import org.chs.domain.docker.entity.PortBindingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortBindingEntityRepository extends JpaRepository<PortBindingEntity, String> {
}
