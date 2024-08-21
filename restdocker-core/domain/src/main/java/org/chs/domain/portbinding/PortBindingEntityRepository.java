package org.chs.domain.portbinding;

import org.chs.domain.portbinding.entity.PortBindingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortBindingEntityRepository extends JpaRepository<PortBindingEntity, String>, CustomPortBindingEntityRepository {
}
