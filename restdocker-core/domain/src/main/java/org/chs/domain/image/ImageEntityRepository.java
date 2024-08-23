package org.chs.domain.image;

import org.chs.domain.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageEntityRepository extends JpaRepository<ImageEntity, String>, CustomImageEntityRepository {
}
