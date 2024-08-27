package org.chs.domain.container;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomContainerEntityRepositoryImpl implements CustomContainerEntityRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long deleteByContainerPkList(List<String> containerPkList) {
        return 1;
    }

    @Override
    public long deleteByImagePk(String pk) {
        return 1;
    }

    // Container Create 시, OauthServiceId, ContainerName 이 복합 Unique 속성인걸 인지
}
