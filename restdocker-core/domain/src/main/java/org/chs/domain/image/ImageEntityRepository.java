package org.chs.domain.image;

import org.chs.domain.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageEntityRepository extends JpaRepository<ImageEntity, String>, CustomImageEntityRepository {

    List<ImageEntity> findImageEntitiesByAccount_Pk(String accountPk);
}
