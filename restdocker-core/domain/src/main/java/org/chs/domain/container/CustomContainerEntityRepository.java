package org.chs.domain.container;

import java.util.List;

public interface CustomContainerEntityRepository {
    long deleteByContainerPkList(List<String> containerPkList);

    long deleteByImagePk(String pk);
}
