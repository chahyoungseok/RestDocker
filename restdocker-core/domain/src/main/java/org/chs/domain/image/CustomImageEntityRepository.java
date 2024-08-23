package org.chs.domain.image;


import org.chs.domain.image.dto.ImageDetailElements;
import org.chs.domain.image.dto.ImageElements;

import java.util.List;

public interface CustomImageEntityRepository {
    List<ImageElements> findAllByOauthServiceId(String oauthServiceId, String imageName);

    ImageDetailElements inspectImage(String oauthServiceId, String imageName);

    boolean rmImage(String oauthServiceId, String imageName);
}
