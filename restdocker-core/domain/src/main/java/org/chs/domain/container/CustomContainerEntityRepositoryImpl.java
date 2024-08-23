package org.chs.domain.container;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomContainerEntityRepositoryImpl implements CustomContainerEntityRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long deleteByImage(String imagePk) {
        return 0;
    }
}
